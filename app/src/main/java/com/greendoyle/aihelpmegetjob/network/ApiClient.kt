package com.greendoyle.aihelpmegetjob.network

import com.greendoyle.aihelpmegetjob.BuildConfig
import com.greendoyle.aihelpmegetjob.aitools.ToolRegistry
import com.greendoyle.aihelpmegetjob.mmkv.StorageManager
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
    private var model: String = "gpt-4o" // 默认模型

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

    /**
     * 从存储中加载配置到内存（每次请求前自动调用）
     */
    private fun ensureConfigLoaded() {
        val aiConfig = StorageManager.getAiConfig()

        val newUri = aiConfig.apiUri
        if (newUri.isNotEmpty() && newUri != baseUrl) {
            val fixed = fixBaseUrl(newUri)
            if (fixed != baseUrl) {
                baseUrl = fixed
                retrofit = null
                okHttpClient = null
            }
        }

        if (aiConfig.apiKey.isNotEmpty() && aiConfig.apiKey != apiKey) {
            apiKey = aiConfig.apiKey
        }

        if (aiConfig.model.isNotEmpty() && aiConfig.model != model) {
            model = aiConfig.model
        }
    }

    /**
     * 校验当前配置是否完整
     * @return null 表示配置完整，否则返回错误原因
     */
    private fun validateConfig(): String? {
        if (baseUrl.isEmpty()) return "API URL 未配置"
        if (apiKey.isEmpty()) return "API Key 未配置"
        if (model.isEmpty()) return "Model 未配置"
        return null
    }

    private fun getOrCreateRetrofit(): Retrofit {
        val existing = retrofit
        if (existing != null) return existing
        synchronized(this) {
            val existing2 = retrofit
            if (existing2 != null) return existing2

            // 单独获取OkHttpClient（可能只是Key变化）
            val client = getOrCreateOkHttpClient()

            val newRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit = newRetrofit
            return newRetrofit
        }
    }

    /**
     * 获取或创建OkHttpClient
     * 如果apiKey有变化，需要创建新的OkHttpClient（因为认证信息在拦截器中）
     */
    private fun getOrCreateOkHttpClient(): OkHttpClient {
        val existing = okHttpClient
        if (existing != null) {
            // 检查是否需要重建（主要是apiKey变化）
            // 这里简化处理，如果okHttpClient存在就直接返回
            return existing
        }

        synchronized(this) {
            val existing2 = okHttpClient
            if (existing2 != null) return existing2

            val client = createOkHttpClient()
            okHttpClient = client
            return client
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)

        // Add auth interceptor — reads apiKey dynamically so it always uses the current value
        val authInterceptor = okhttp3.Interceptor { chain ->
            val currentKey = apiKey
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $currentKey")
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

    suspend fun testConnectivity(
        overrideUrl: String? = null,
        overrideKey: String? = null,
        overrideModel: String? = null
    ): Result<String> {
        // If overrides are provided, use them directly; otherwise load from storage
        if (overrideUrl != null) baseUrl = fixBaseUrl(overrideUrl) else ensureConfigLoaded()
        if (overrideKey != null) apiKey = overrideKey
        if (overrideModel != null) model = overrideModel

        val configError = validateConfig()
        if (configError != null) {
            return Result.failure(IllegalArgumentException(configError))
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
     * @param systemPrompt 系统提示词（可选）
     * @param temperature 温度参数
     * @return LLM 返回的响应内容
     */
    suspend fun chatWithLLm(
        jdPrompt: String,
        systemPrompt: String? = null,
        temperature: Double = 0.7
    ): Result<String> {
        if (jdPrompt.isBlank()) {
            return Result.failure(IllegalArgumentException("Prompt cannot be empty"))
        }

        ensureConfigLoaded()
        val configError = validateConfig()
        if (configError != null) {
            return Result.failure(IllegalArgumentException(configError))
        }

        // Determine which system prompt to use
        val messages = if (!systemPrompt.isNullOrBlank()) {
            listOf(
                Message(role = "system", content = systemPrompt),
                Message(role = "user", content = jdPrompt)
            )
        } else {
            listOf(Message(role = "user", content = jdPrompt))
        }

        return try {
            val response = api.chatCompletion(
                ChatCompletionRequest(
                    model = this.model,
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
        temperature: Double = 0.7
    ): Result<List<String>> {
        if (jdPrompts.isEmpty()) {
            return Result.failure(IllegalArgumentException("JD prompts list cannot be empty"))
        }

        ensureConfigLoaded()
        val configError = validateConfig()
        if (configError != null) {
            return Result.failure(IllegalArgumentException(configError))
        }

        val results = mutableListOf<String>()
        val currentModel = this.model

        for ((index, prompt) in jdPrompts.withIndex()) {
            if (prompt.isBlank()) {
                results.add("[Empty prompt at index $index]")
                continue
            }

            val messages = if (!systemPrompt.isNullOrBlank()) {
                listOf(
                    Message(role = "system", content = systemPrompt),
                    Message(role = "user", content = prompt)
                )
            } else {
                listOf(Message(role = "user", content = prompt))
            }

            try {
                val response = api.chatCompletion(
                    ChatCompletionRequest(
                        model = currentModel,
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
