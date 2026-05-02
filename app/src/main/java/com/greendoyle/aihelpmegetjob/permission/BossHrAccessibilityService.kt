package com.greendoyle.aihelpmegetjob.permission

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.greendoyle.aihelpmegetjob.page_monitor.BossZhiPinPageMonitor
import com.greendoyle.aihelpmegetjob.utils.UiTreeTraverser

class BossHrAccessibilityService : AccessibilityService() {

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var scanRunnable: Runnable? = null

    // 防抖触发扫描
    private fun debounceScanCards(rootNode: AccessibilityNodeInfo?) {
        // 先移除之前的任务，避免重复执行
        scanRunnable?.let {
            handler.removeCallbacks(it)
            scanRunnable = null
        }

        // 新建延迟任务
        val runnable = Runnable {
            UiTreeTraverser.scanAllJobCards(rootNode)
        }

        scanRunnable = runnable
        // 延迟 500ms 执行（可自己调 300~800）
        handler.postDelayed(runnable, 500)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 1. 获取当前界面根节点
        val rootNode = rootInActiveWindow ?: return

        // 2. 判断：是否是 BOSS APP + 是否是职位页面
        val isBossApp = BossZhiPinPageMonitor.isBossApp(event)
        val isJobPage = BossZhiPinPageMonitor.isJobPage(rootNode)

        // 3. 只有 【BOSS + 职位页面】 才解析
        if (isBossApp && isJobPage) {
            // UiTreeTraverser.traverseTree(rootNode)
            debounceScanCards(rootNode)
        }
    }

    override fun onInterrupt() {}
}