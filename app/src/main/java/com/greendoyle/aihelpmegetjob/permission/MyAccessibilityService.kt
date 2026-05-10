package com.greendoyle.aihelpmegetjob.permission

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.greendoyle.aihelpmegetjob.page_monitor.BossZhiPinPageMonitor
import com.greendoyle.aihelpmegetjob.utils.UiTreeTraverser
//import com.greendoyle.aihelpmegetjob.utils.LogTool

@SuppressLint("AccessibilityPolicy")
class MyAccessibilityService : AccessibilityService() {

    private val TAG = "MyAccessibilityService"

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var scanRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // serviceInfo 在 onServiceConnected 时才由系统完成初始化
        setServiceInfo(serviceInfo)
        AccessibilityHolder.updateState(true)
    }

        // 防抖触发扫描
    private fun debounceScanCards(rootNode: AccessibilityNodeInfo?) {
        // 先移除之前的任务，避免重复执行
        scanRunnable?.let {
            handler.removeCallbacks(it)
            scanRunnable = null
        }

        // 新建延迟任务
        val runnable = Runnable {
            UiTreeTraverser.traverseTree(rootNode)
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
        if (isBossApp) {
            if(isJobPage) {
                // UiTreeTraverser.traverseTree(rootNode)
                debounceScanCards(rootNode)
            }
            else if(BossZhiPinPageMonitor.hasJobDetailFeature(rootNode))
            {
                // TODO: 先获取当前页面所有文字

                // TODO: 传递给Agent.setJobCard

                // TODO: call Agent.analyze
            }
        }
//        LogTool.d(TAG, "onAccessibilityEvent")
    }

    override fun onInterrupt() {
//        LogTool.d(TAG, "onInterrupt")
    }
    override fun onDestroy() {
        super.onDestroy()
        AccessibilityHolder.updateState(false)
//        LogTool.d(TAG, "onDestroy")
    }
}
