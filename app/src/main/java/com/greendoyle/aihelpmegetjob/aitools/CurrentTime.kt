package com.greendoyle.aihelpmegetjob.aitools

import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CurrentTimeTool : Tool() {
    override val name = "current_time"
    override val description = "Get the current date and time"

    // Gson 直接把 JSON 字符串转成 Map<String, Any>
    override val parameters: Map<String, Any> by lazy {
        val json = """
        {
            "type": "function",
            "function": {
                "name": "current_time",
                "description": "Get the current date and time",
                "parameters": {
                    "type": "object",
                    "properties": {},
                    "required": []
                }
            }
        }
        """.trimIndent()

        // Gson 一行转 Map
        Gson().fromJson(json, Map::class.java) as Map<String, Any>
    }

    // 执行方法
    override suspend fun execute(kwargs: Map<String, Any?>): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}