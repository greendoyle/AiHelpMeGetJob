package com.greendoyle.aihelpmegetjob.permission

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
}