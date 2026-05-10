package com.greendoyle.aihelpmegetjob.permission

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.greendoyle.aihelpmegetjob.page_monitor.BossZhiPinPageMonitor
import com.greendoyle.aihelpmegetjob.utils.LogTool
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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 唯一接收事件
        val rootGetter = { rootInActiveWindow }

        // 分发给 BOSS 监听
        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        {
            LogTool.d(TAG, "onAccessibilityEvent dist window state changed")
            BossZhiPinPageMonitor.onAccessibilityEvent(event, rootGetter)
        }
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY // 保活
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
