package com.greendoyle.aihelpmegetjob.agent

import com.greendoyle.aihelpmegetjob.mmkv.StorageManager
import com.greendoyle.aihelpmegetjob.network.ApiClient
import com.greendoyle.aihelpmegetjob.network.Message
import com.greendoyle.aihelpmegetjob.utils.LogTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Agent 类：整合职位卡片数据和 LLM API，提供智能分析能力
 * ┌─────────────────────────────────────────────────┐
│                    Agent 类                        │
│  ┌─────────────────────────────────────────────┐   │
│  │  入口方法                                   │   │
│  │  - onJobCardClicked()  [suspend]  点击触发  │   │
│  └─────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────┐   │
│  │  核心逻辑                                   │   │
│  │  - analyze()       [suspend]  执行分析      │   │
│  │  - analyzeInternal() [suspend]  内部逻辑    │   │
│  └─────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────┐   │
│  │  配置方法                                   │   │
│  │  - setJobCardText()    设置卡片数据         │   │
│  │  - reset()          重置状态                │   │
│  └─────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────┘
 */
object Agent {

    private val TAG = "Agent"

    // 对话历史（用于上下文）
    private val conversationHistory = mutableListOf<Message>()
    private val agentSystemPrompt = "你是一名求职规划分析师，根据用户简历内容和岗位 JD，按以下固定格式输出，全程精简、每句话不超过 15 字，不要多余话术、不要换行过多：匹配度评分：XX 分（满分 100）匹配亮点：一句话精简概括待提升点：一句话精简概括"

    // 当前分析的职位卡片
    var currentJobCardText: String? = null

    // 分析结果
    var analysisResult: String? = null

    // 分析状态
    var isAnalyzing = false

    // 悬浮窗（object 静态类）

    /**
     * 设置待分析的职位卡片
     */
    fun setJobCardText(cardText: String) {
        this.currentJobCardText = cardText
        conversationHistory.clear()
        analysisResult = null
        isAnalyzing = false
    }

    /**
     * 添加系统提示词
     */
    fun addSystemPrompt(prompt: String) {
        conversationHistory.add(
            Message(role = "system", content = prompt)
        )
    }

    /**
     * 添加用户问题
     */
    fun addUserQuestion(question: String) {
        conversationHistory.add(
            Message(role = "user", content = question)
        )
    }

    /**
     * 执行分析（同步调用，内部使用协程）
     */
    suspend fun analyze(): String {
        return withContext(Dispatchers.IO) {
           analyzeInternal()
        }
    }

    /**
     * 直接传入职位文本进行分析
     */
    suspend fun analyze(jobText: String): String {
        setJobCardText(jobText)
        return analyze()
    }

    /**
     * 内部分析逻辑
     */
    private suspend fun analyzeInternal(): String {
        isAnalyzing = true
        LogTool.d(TAG, "开始分析职位卡片")

        try {
            val resume = StorageManager.getResumeInfo()
            val filter = StorageManager.getFilterConfig()

            val jdPrompt = buildString {
                append("请分析以下岗位JD：\n\n")
                append("内容：${currentJobCardText}\n")

                append("求职者技能：\n")
                if (resume.coreSkills.isNotBlank()) append("核心技能：${resume.coreSkills}\n")
                if (resume.workYears.isNotBlank()) append("工作年限：${resume.workYears}\n")
                if (resume.education.isNotBlank()) append("学历：${resume.education}\n")
                if (resume.jobIntent.isNotBlank()) append("求职意向：${resume.jobIntent}\n")

                append("求职者需求：\n")
                if (filter.city.isNotBlank()) append("期望城市：${filter.city}\n")
                if (filter.salaryMin.isNotBlank() || filter.salaryMax.isNotBlank()) {
                    append("期望薪资：${filter.salaryMin}-${filter.salaryMax}\n")
                }
                if (filter.acceptOutsourcing.isNotBlank() && filter.acceptOutsourcing != "不限") {
                    append("外包要求：${filter.acceptOutsourcing}\n")
                }
            }

            val result = ApiClient.chatWithLLm(jdPrompt, systemPrompt = agentSystemPrompt)

            if (result.isFailure) {
                val error = result.exceptionOrNull()?.message ?: "未知错误"
                LogTool.e(TAG, "API 错误：$error")
                analysisResult = "分析失败：$error"
            } else {
                val aiResponse = result.getOrNull() ?: ""
                analysisResult = aiResponse
                LogTool.d(TAG, "分析完成，结果长度：${aiResponse.length}")
            }
        } catch (e: Exception) {
            analysisResult = "分析失败：${e.message}"
            LogTool.e(TAG, analysisResult)
        } finally {
            isAnalyzing = false
        }

        return analysisResult ?: ""
    }

    /**
     * 重置所有状态
     */
    fun reset() {
        currentJobCardText = null
        analysisResult = null
        isAnalyzing = false
        conversationHistory.clear()
    }

    /**
     * 关闭悬浮窗（在 Activity 销毁时调用）
     */
    fun closePopup() {
    }
}
