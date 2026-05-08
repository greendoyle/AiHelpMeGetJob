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
    fun hasJobDetailFeature(rootNode: AccessibilityNodeInfo): Boolean {
        return containText(rootNode, "职位详情")
                || containText(rootNode, "岗位职责")
                || containText(rootNode, "任职要求")
    }

    /**
     * 工具：递归遍历所有节点，判断是否包含指定文字
     */
    fun containText(node: AccessibilityNodeInfo?, text: String): Boolean {
        if (node == null) return false
        if (node.text?.contains(text, true) == true) return true
        
        for (i in 0 until node.childCount) {
            if (containText(node.getChild(i), text)) return true
        }
        return false
    }
}