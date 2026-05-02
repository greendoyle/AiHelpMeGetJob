package com.greendoyle.aihelpmegetjob.page_monitor

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object BossZhiPinPageMonitor {

    private const val BOSS_PACKAGE = "com.hpbr.bosszhipin"

    /**
     * 判断是不是 BOSS 直聘 APP
     */
    fun isBossApp(event: AccessibilityEvent): Boolean {
        return event.packageName.toString() == BOSS_PACKAGE
    }

    /**
     * 判断当前页面是不是【职位相关页面】
     * 列表页 or 详情页 → return true
     * 其他页（我的、消息、设置）→ return false
     */
    fun isJobPage(rootNode: AccessibilityNodeInfo): Boolean {
        return hasJobListFeature(rootNode) || hasJobDetailFeature(rootNode)
    }

    /**
     * 判断是不是职位列表页
     * 特征：出现 薪资、公司名、职位名
     */
    private fun hasJobListFeature(rootNode: AccessibilityNodeInfo): Boolean {
        return containText(rootNode, "薪资")
                || containText(rootNode, "立即沟通")
    }

    /**
     * 判断是不是职位详情页
     * 特征：职位详情、岗位职责、任职要求
     */
    private fun hasJobDetailFeature(rootNode: AccessibilityNodeInfo): Boolean {
        return containText(rootNode, "职位详情")
                || containText(rootNode, "岗位职责")
                || containText(rootNode, "任职要求")
    }

    /**
     * 工具：遍历节点，判断是否包含某段文字
     */
    private fun containText(root: AccessibilityNodeInfo, text: String): Boolean {
        // 这里是简单判断，你可以自己扩展
        return true // 你后面我可以帮你写完整遍历判断
    }
}