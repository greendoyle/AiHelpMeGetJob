package com.greendoyle.aihelpmegetjob.permission

import android.content.Context

object AccessibilityHolder {
    // 服务是否真正运行、可用
    @Volatile
    var isServiceAvailable: Boolean = false
        private set

    // 状态回调
    var onServiceAvailableChanged: ((Boolean) -> Unit)? = null

    @Synchronized
    fun updateState(available: Boolean) {
        isServiceAvailable = available
        onServiceAvailableChanged?.invoke(available)
    }

    /**
     * 从系统设置读取无障碍服务是否已开启，用于应用重启后恢复状态。
     * onServiceConnected 仅在服务首次连接时调用一次，重启后不会再次触发。
     */
    @Synchronized
    fun syncFromSystem(context: Context) {
        if (PermissionHelper.isAccessibilityServiceEnabled(context, MyAccessibilityService::class.java)) {
            isServiceAvailable = true
            onServiceAvailableChanged?.invoke(true)
        }
    }
}
