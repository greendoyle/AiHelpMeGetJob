package com.greendoyle.aihelpmegetjob.network

import com.greendoyle.aihelpmegetjob.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// --- API Interface ---

data class ChatCompletionRequest(
    val model: String = "",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String = "user",
    val content: String
)

data class ChatCompletionResponse(
    val choices: List<Choice>,
    val error: ApiError? = null
)

data class Choice(
    val message: Message
)

data class ApiError(
    val message: String? = null,
    val code: String? = null
)

interface OpenAIApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

// --- ApiClient ---

object ApiClient {

    private const val DEFAULT_TIMEOUT = 30L

    @Volatile
    private var baseUrl: String = ""

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var okHttpClient: OkHttpClient? = null

    @Volatile
    private var apiKey: String = ""

    val api: OpenAIApi
        get() = getOrCreateRetrofit().create()

    private fun fixBaseUrl(baseUrl: String): String {
        if (baseUrl.isBlank()) return baseUrl
        return if (baseUrl.endsWith("/")) {
            baseUrl
        } else {
            "$baseUrl/"
        }
    }

    fun setBaseUrl(url: String) {
        val fixedUrl = fixBaseUrl(url)
        if (baseUrl != fixedUrl) {
            baseUrl = fixedUrl
            retrofit = null
            okHttpClient = null
        }
    }

    fun getBaseUrl(): String = baseUrl

    fun setApiKey(key: String) {
        apiKey = key
    }

    fun getApiKey(): String = apiKey

    private fun getOrCreateRetrofit(): Retrofit {
        val existing = retrofit
        if (existing != null) return existing
        synchronized(this) {
            val existing2 = retrofit
            if (existing2 != null) return existing2
            val client = createOkHttpClient()
            val newRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit = newRetrofit
            okHttpClient = client
            return newRetrofit
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)

        // Add auth interceptor
        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        builder.addInterceptor(authInterceptor)

        // Enable logging in debug builds
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    suspend fun testConnectivity(key: String, model: String = "gpt-3.5-turbo"): Result<String> {
        // Set the API key before making the test call
        setApiKey(key)

        // Validate base URL is set
        if (baseUrl.isEmpty()) {
            return Result.failure(IllegalArgumentException("Base URL not configured"))
        }

        return try {
            val response = api.chatCompletion(
                ChatCompletionRequest(
                    model = model.ifEmpty { "gpt-3.5-turbo" },
                    messages = listOf(Message(role = "user", content = "Hi")),
                    temperature = 0.7
                )
            )
            if (response.error != null) {
                Result.failure(Exception("API error: ${response.error.message}"))
            } else {
                Result.success("OK")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
