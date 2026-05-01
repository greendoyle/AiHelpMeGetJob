package com.greendoyle.aihelpmegetjob.permission

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
//import com.greendoyle.aihelpmegetjob.utils.LogTool

@SuppressLint("AccessibilityPolicy")
class MyAccessibilityService : AccessibilityService() {
    private val TAG = "MyAccessibilityService"
    override fun onServiceConnected() {
        super.onServiceConnected()
//        LogTool.d(TAG, "onServiceConnected")
        AccessibilityHolder.updateState(true)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
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