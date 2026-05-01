package com.greendoyle.aihelpmegetjob.parser

import android.view.accessibility.AccessibilityNodeInfo
import com.greendoyle.aihelpmegetjob.data.model.RecruitJob
import com.greendoyle.aihelpmegetjob.utils.LogTool

/**
 * Boss直聘职位解析器
 * 通过无障碍服务解析Boss直聘APP的职位详情页
 */
class BossZhiPinParser : RecruitAppParser {

    private val TAG = "BossZhiPinParser"
    companion object {
        private const val SOURCE_APP = "BOSS直聘"
    }

    // 递归遍历（带中止）
    fun traverseCheck(node: AccessibilityNodeInfo?, action: (AccessibilityNodeInfo) -> Unit) {
        node ?: return
        try {
            action(node)
            for (i in 0 until node.childCount) {
                traverseCheck(node.getChild(i), action)
            }
        } finally {
            // 注意：外层 root 不回收，内部子节点回收
        }
    }
    private fun hasAnyText(root: AccessibilityNodeInfo, keywords: List<String>): Boolean {
        val found = BooleanArray(1)
        traverseCheck(root) { node ->
            val text = node.text?.toString() ?: ""
            for (kw in keywords) {
                if (text.contains(kw)) {
                    found[0] = true
                    return@traverseCheck
                }
            }
        }
        return found[0]
    }
    private fun isJobListPage(root: AccessibilityNodeInfo): Boolean {
        return hasAnyText(root, listOf("推荐", "附近", "最新"))
    }
    private fun isJobDetailPage(root: AccessibilityNodeInfo): Boolean {
        return hasAnyText(root, listOf("职位详情"))
    }
    override fun parse(rootNode: AccessibilityNodeInfo) {
        if(isJobListPage(rootNode))
        {

        }
        if(isJobDetailPage(rootNode)) {
            val job = parseJobListItem(rootNode)
        }

    }
    /**
     * 解析职位列表页
     * @param rootNode 无障碍根节点
     * @return 职位列表
     */
    override fun parseJobList(rootNode: AccessibilityNodeInfo): List<RecruitJob> {
        val jobList = mutableListOf<RecruitJob>()

        // Boss直聘职位列表项通常包含在RecyclerView中
        // 遍历所有可能的职位卡片
        val jobCards = findNodesByClassName(rootNode, "android.widget.TextView")
            .filter { it.text?.contains("K") == true || it.text?.contains("元") == true }

        jobCards.forEach { node ->
            try {
                val job = parseJobListItem(node)
                if (job != null) {
                    jobList.add(job)
                }
            } catch (e: Exception) {
                // 单个解析失败不影响其他
            }
        }

        return jobList
    }

    /**
     * 解析职位详情页
     * @param rootNode 无障碍根节点
     * @return 职位详情,解析失败返回null
     */
    override fun parseJobDetail(rootNode: AccessibilityNodeInfo): RecruitJob? {
        try {
            return RecruitJob(
                // 1. 基础信息
                jobName = extractJobName(rootNode) ?: "",
                companyName = extractCompanyName(rootNode) ?: "",
                salary = extractSalary(rootNode) ?: "",
                cityArea = extractCityArea(rootNode) ?: "",

                // 2. 岗位要求
                workYears = extractWorkYears(rootNode) ?: "",
                education = extractEducation(rootNode) ?: "",
                jobTags = extractJobTags(rootNode),

                // 3. 公司信息
                companyType = extractCompanyType(rootNode) ?: "",
                companySize = extractCompanySize(rootNode) ?: "",

                // 4. 职位详情
                jobRequirement = extractJobRequirement(rootNode) ?: "",
                jobResponsibility = extractJobResponsibility(rootNode) ?: "",

                // 5. 业务扩展字段
                sourceApp = SOURCE_APP,
                isApplied = checkIsApplied(rootNode)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 提取职位名称
     */
    private fun extractJobName(rootNode: AccessibilityNodeInfo): String? {
        // Boss直聘职位名称通常在详情页顶部
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { it.text?.length ?: 0 > 2 && it.text?.length ?: 0 < 30 }
            ?.text?.toString()
    }

    /**
     * 提取公司名称
     */
    private fun extractCompanyName(rootNode: AccessibilityNodeInfo): String? {
        // 公司名称通常包含"公司"、"有限"等关键词
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { 
                val text = it.text?.toString() ?: ""
                (text.contains("公司") || text.contains("有限")) && text.length < 50
            }
            ?.text?.toString()
    }

    /**
     * 提取薪资信息
     */
    private fun extractSalary(rootNode: AccessibilityNodeInfo): String? {
        // 薪资通常包含"K"、"元"、"万"等关键词
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { 
                val text = it.text?.toString() ?: ""
                (text.contains("K") || text.contains("元") || text.contains("万")) && 
                text.matches(Regex(".*/d+.*"))
            }
            ?.text?.toString()
    }

    /**
     * 提取城市和区域
     */
    private fun extractCityArea(rootNode: AccessibilityNodeInfo): String? {
        // 城市区域通常包含"·"、"市"、"区"等
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { 
                val text = it.text?.toString() ?: ""
                (text.contains("·") || text.contains("市") || text.contains("区")) && 
                text.length < 20
            }
            ?.text?.toString()
    }

    /**
     * 提取工作经验要求
     */
    private fun extractWorkYears(rootNode: AccessibilityNodeInfo): String? {
        // 工作经验通常包含"年"、"经验"等关键词
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { 
                val text = it.text?.toString() ?: ""
                text.contains("年") && (text.contains("经验") || text.matches(Regex("/d+-?/d*年")))
            }
            ?.text?.toString()
    }

    /**
     * 提取学历要求
     */
    private fun extractEducation(rootNode: AccessibilityNodeInfo): String? {
        // 学历通常包含"本科"、"硕士"、"博士"、"大专"等关键词
        val educationKeywords = listOf("本科", "硕士", "博士", "大专", "高中", "初中", "学历")
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { 
                val text = it.text?.toString() ?: ""
                educationKeywords.any { text.contains(it) }
            }
            ?.text?.toString()
    }

    /**
     * 提取职位标签
     */
    private fun extractJobTags(rootNode: AccessibilityNodeInfo): List<String> {
        val tags = mutableListOf<String>()
        // 标签通常在详情页中,以列表形式展示
        findNodesByClassName(rootNode, "android.widget.TextView")
            .filter { 
                val text = it.text?.toString() ?: ""
                text.length < 10 && !text.contains("年") && !text.contains("K")
            }
            .forEach { 
                it.text?.toString()?.let { tags.add(it) }
            }
        return tags
    }

    /**
     * 提取公司类型
     */
    private fun extractCompanyType(rootNode: AccessibilityNodeInfo): String? {
        // 公司类型通常包含"互联网"、"金融"、"教育"等行业关键词
        val industryKeywords = listOf("互联网", "金融", "教育", "医疗", "制造", "贸易", "科技")
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { 
                val text = it.text?.toString() ?: ""
                industryKeywords.any { text.contains(it) }
            }
            ?.text?.toString()
    }

    /**
     * 提取公司规模
     */
    private fun extractCompanySize(rootNode: AccessibilityNodeInfo): String? {
        // 公司规模通常包含"人"字,如"100-499人"
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .firstOrNull { 
                val text = it.text?.toString() ?: ""
                text.contains("人") && text.matches(Regex(".*/d+.*人.*"))
            }
            ?.text?.toString()
    }

    /**
     * 提取岗位要求
     */
    private fun extractJobRequirement(rootNode: AccessibilityNodeInfo): String? {
        // 岗位要求通常在详情页中,文字较长
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .filter { 
                val text = it.text?.toString() ?: ""
                text.length > 50 && (text.contains("要求") || text.contains("技能"))
            }
            .map { it.text?.toString() }
            .firstOrNull()
    }

    /**
     * 提取岗位职责
     */
    private fun extractJobResponsibility(rootNode: AccessibilityNodeInfo): String? {
        // 岗位职责通常在详情页中,文字较长
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .filter { 
                val text = it.text?.toString() ?: ""
                text.length > 50 && (text.contains("职责") || text.contains("负责"))
            }
            .map { it.text?.toString() }
            .firstOrNull()
    }

    /**
     * 检查是否已投递
     */
    private fun checkIsApplied(rootNode: AccessibilityNodeInfo): Boolean {
        // 检查是否存在"已投递"、"已沟通"等状态文字
        val appliedKeywords = listOf("已投递", "已沟通", "已面试")
        return findNodesByClassName(rootNode, "android.widget.TextView")
            .any { 
                val text = it.text?.toString() ?: ""
                appliedKeywords.any { text.contains(it) }
            }
    }

    /**
     * 解析单个职位列表项
     */
    private fun parseJobListItem(node: AccessibilityNodeInfo): RecruitJob? {
        val salary = node.text?.toString() ?: return null
        LogTool.d(TAG, salary)
        return RecruitJob(
            salary = salary,
            sourceApp = SOURCE_APP
        )
    }

    /**
     * 根据类名查找节点
     */
    private fun findNodesByClassName(rootNode: AccessibilityNodeInfo, className: String): List<AccessibilityNodeInfo> {
        val nodes = mutableListOf<AccessibilityNodeInfo>()
        if (rootNode.className?.toString() == className) {
            nodes.add(rootNode)
        }

        for (i in 0 until rootNode.childCount) {
            nodes.addAll(findNodesByClassName(rootNode.getChild(i), className))
        }
        return nodes
    }
}
