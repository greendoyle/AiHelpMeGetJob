package com.greendoyle.aihelpmegetjob.mmkv


import com.google.gson.Gson
import com.greendoyle.aihelpmegetjob.data.model.*
import com.greendoyle.aihelpmegetjob.utils.AppDispatch
import com.greendoyle.aihelpmegetjob.utils.LogTool
import com.tencent.mmkv.MMKV

object StorageManager {

    private val mmkv: MMKV = MMKV.defaultMMKV()
    private val gson = Gson()
    private const val TAG = "StorageManager"

    private fun maskApiKey(apiKey: String): String {
        if (apiKey.length <= 8) {
            return "*".repeat(apiKey.length) // 太短全遮
        }
        // 前 4 位可见 + 中间全部 * + 最后 4 位可见
        val visibleStart = apiKey.take(4)
        val visibleEnd = apiKey.takeLast(4)
        val starCount = apiKey.length - 8
        val stars = "*".repeat(starCount.coerceAtLeast(0))

        return "$visibleStart$stars$visibleEnd"
    }
    // --- Basic types ---

    fun putString(key: String, value: String?) {
        if (value == null) mmkv.remove(key) else mmkv.encode(key, value)
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return mmkv.decodeString(key, defaultValue) ?: defaultValue
    }

    fun putBoolean(key: String, value: Boolean) {
        mmkv.encode(key, value)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        mmkv.encode(key, value)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        mmkv.encode(key, value)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    // --- Complex objects via Gson ---

    fun saveResumeInfo(resumeInfo: ResumeInfo) {
        val json = gson.toJson(resumeInfo)
        mmkv.encode("resume_info", json)
    }

    fun getResumeInfo(): ResumeInfo {
        val json = mmkv.decodeString("resume_info", null)
            ?: return ResumeInfo()
        return try {
            gson.fromJson(json, ResumeInfo::class.java)
        } catch (_: Exception) {
            ResumeInfo()
        }
    }

    fun saveAiConfig(aiConfig: AiConfig) {
        // 丢后台：序列化、打码、打印、存储 全在子线程
        AppDispatch.launchIO {
            // 真实完整JSON用于本地保存
            val realJson = LogTool.gson.toJson(aiConfig)

            // data class copy 替换打码后的 apiKey（完美支持 val）
            val debugConfig = aiConfig.copy(
                apiKey = LogTool.maskApiKey(aiConfig.apiKey)
            )

            // 格式化打印打码后的对象
            LogTool.obj(TAG, debugConfig)

            // MMKV 耗时存储放后台
            mmkv.encode("ai_config", realJson)
        }
    }

    fun getAiConfig(): AiConfig {
        val json = mmkv.decodeString("ai_config", null)
            ?: return AiConfig()
        return try {
            gson.fromJson(json, AiConfig::class.java)
        } catch (_: Exception) {
            AiConfig()
        }
    }

    fun saveFilterConfig(filterConfig: FilterConfig) {
        val json = gson.toJson(filterConfig)
        mmkv.encode("filter_config", json)
    }

    fun getFilterConfig(): FilterConfig {
        val json = mmkv.decodeString("filter_config", null)
            ?: return FilterConfig()
        return try {
            gson.fromJson(json, FilterConfig::class.java)
        } catch (_: Exception) {
            FilterConfig()
        }
    }

    fun saveHistoryRecord(record: HistoryRecord) {
        val json = gson.toJson(record)
        mmkv.encode("history_record_${record.id}", json)
    }

    fun getHistoryRecords(): List<HistoryRecord> {
        val records = mutableListOf<HistoryRecord>()
        val allKeys = mmkv.allKeys() ?: return records
        for (key in allKeys) {
            if (key.startsWith("history_record_")) {
                val json = mmkv.decodeString(key, null) ?: continue
                try {
                    val record = gson.fromJson(json, HistoryRecord::class.java)
                    records.add(record)
                } catch (_: Exception) {
                    // Skip malformed entries
                }
            }
        }
        return records.sortedByDescending { it.timestamp }
    }

    // --- Delete / Clear ---

    fun remove(key: String) {
        mmkv.remove(key)
    }

    fun removeHistoryRecord(id: Long) {
        mmkv.remove("history_record_$id")
    }

    fun clear() {
        mmkv.clearAll()
    }
}
