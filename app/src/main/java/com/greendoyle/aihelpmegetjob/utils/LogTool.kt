package com.greendoyle.aihelpmegetjob.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object LogTool {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    // 普通调试日志（自动后台）
    fun d(tag: String, msg: String) {
        AppDispatch.launchIO {
            Log.d(tag, msg)
        }
    }

    // 普通调试日志（自动后台）
    fun i(tag: String, msg: String) {
        AppDispatch.launchIO {
            Log.i(tag, msg)
        }
    }
    // 错误日志
    fun e(tag: String, msg: String) {
        AppDispatch.launchIO {
            Log.e(tag, msg)
        }
    }

    // 格式化打印 JSON 字符串
    fun json(tag: String, jsonStr: String) {
        AppDispatch.launchIO {
            try {
                val parse = gson.fromJson(jsonStr, Any::class.java)
                val pretty = gson.toJson(parse)
                Log.d(tag, "$pretty")
            } catch (e: Exception) {
                Log.e(tag, "JSON格式错误: $jsonStr")
            }
        }
    }

    // 打印任意对象，自动转格式化JSON
    fun obj(tag: String, any: Any) {
        AppDispatch.launchIO {
            val pretty = gson.toJson(any)
            Log.d(tag, "$pretty")
        }
    }

    // 对 API Key 打码：前4位 + 中间星号 + 后4位
    fun maskApiKey(key: String): String {
        if (key.length <= 8) {
            return "*".repeat(key.length.coerceAtMost(16))
        }
        val start = key.take(4)
        val end = key.takeLast(4)
        val stars = "*".repeat((key.length - 8).coerceAtLeast(0))
        return "$start$stars$end"
    }
}