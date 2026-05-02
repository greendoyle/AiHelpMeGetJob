package com.greendoyle.aihelpmegetjob.permission

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.greendoyle.aihelpmegetjob.page_monitor.BossZhiPinPageMonitor
import com.greendoyle.aihelpmegetjob.parser.BossZhiPinParser
import com.greendoyle.aihelpmegetjob.utils.UiTreeTraverser

class BossHrAccessibilityService : AccessibilityService() {

    private val nodeParser = BossZhiPinParser()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 1. 获取当前界面根节点
        val rootNode = rootInActiveWindow ?: return

        // 2. 判断：是否是 BOSS APP + 是否是职位页面
        val isBossApp = BossZhiPinPageMonitor.isBossApp(event)
        val isJobPage = BossZhiPinPageMonitor.isJobPage(rootNode)

        // 3. 只有 【BOSS + 职位页面】 才解析
        if (isBossApp && isJobPage) {
//            nodeParser.parse(rootNode)
            // UiTreeTraverser.traverseTree(rootNode)
            UiTreeTraverser.scanAllJobCards(rootNode)
        }
    }

    override fun onInterrupt() {}
}