package com.greendoyle.aihelpmegetjob.network

import com.greendoyle.aihelpmegetjob.BuildConfig
import com.greendoyle.aihelpmegetjob.aitools.ToolRegistry
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
    val tools: List<Map<String, Any>>,
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

    @Volatile
    private var systemPrompt: String = ""

    private val api: OpenAIApi
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

    fun getSystemPrompt(): String = systemPrompt

    fun setSystemPrompt(prompt: String) {
        systemPrompt = prompt
    }

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
                    tools = ToolRegistry.getOpenaiToolsSchema(),
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

    /**
     * 与 LLM API 通信
     * @param jdPrompt 职位描述/问题 prompt
     * @param systemPrompt 系统提示词（可选，默认使用全局设置的 systemPrompt）
     * @param model 模型名称（可选，默认使用全局配置）
     * @return LLM 返回的响应内容
     */
    suspend fun chatWithLLM(
        jdPrompt: String,
        systemPrompt: String? = null,
        model: String = "gpt-4o",
        temperature: Double = 0.7
    ): Result<String> {
        // Validate inputs
        if (jdPrompt.isBlank()) {
            return Result.failure(IllegalArgumentException("JD prompt cannot be empty"))
        }

        // Validate base URL is set
        if (baseUrl.isEmpty()) {
            return Result.failure(IllegalArgumentException("Base URL not configured"))
        }

        // Determine which system prompt to use
        val effectiveSystemPrompt = systemPrompt ?: this.systemPrompt

        return try {
            // Build messages with system prompt + user prompt
            val messages = if (effectiveSystemPrompt.isNotBlank()) {
                listOf(
                    Message(role = "system", content = effectiveSystemPrompt),
                    Message(role = "user", content = jdPrompt)
                )
            } else {
                listOf(Message(role = "user", content = jdPrompt))
            }

            // Make API call
            val response = api.chatCompletion(
                ChatCompletionRequest(
                    model = model.ifEmpty { "gpt-4o" },
                    messages = messages,
                    tools = ToolRegistry.getOpenaiToolsSchema(),
                    temperature = temperature
                )
            )

            if (response.error != null) {
                Result.failure(Exception("API error: ${response.error.message}"))
            } else {
                val content = response.choices.firstOrNull()?.message?.content ?: ""
                Result.success(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量处理多个 JD prompt
     * @param jdPrompts 职位描述/问题列表
     * @param systemPrompt 系统提示词（可选）
     * @return 每个 prompt 对应的响应列表
     */
    suspend fun chatBatch(
        jdPrompts: List<String>,
        systemPrompt: String? = null,
        model: String = "gpt-4o",
        temperature: Double = 0.7
    ): Result<List<String>> {
        if (jdPrompts.isEmpty()) {
            return Result.failure(IllegalArgumentException("JD prompts list cannot be empty"))
        }

        val results = mutableListOf<String>()

        for ((index, prompt) in jdPrompts.withIndex()) {
            if (prompt.isBlank()) {
                results.add("[Empty prompt at index $index]")
                continue
            }

            val effectiveSystemPrompt = systemPrompt ?: this.systemPrompt
            val messages = if (effectiveSystemPrompt.isNotBlank()) {
                listOf(
                    Message(role = "system", content = effectiveSystemPrompt),
                    Message(role = "user", content = prompt)
                )
            } else {
                listOf(Message(role = "user", content = prompt))
            }

            try {
                val response = api.chatCompletion(
                    ChatCompletionRequest(
                        model = model.ifEmpty { "gpt-4o" },
                        messages = messages,
                        tools = ToolRegistry.getOpenaiToolsSchema(),
                        temperature = temperature
                    )
                )

                if (response.error != null) {
                    results.add("[Error at index $index]: ${response.error.message}")
                } else {
                    val content = response.choices.firstOrNull()?.message?.content ?: ""
                    results.add(content)
                }
            } catch (e: Exception) {
                results.add("[Exception at index $index]: ${e.message}")
            }
        }

        return Result.success(results)
    }
}
