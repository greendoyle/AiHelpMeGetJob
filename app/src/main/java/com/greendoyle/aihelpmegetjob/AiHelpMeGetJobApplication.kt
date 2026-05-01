package com.greendoyle.aihelpmegetjob

import android.app.Application
import com.tencent.mmkv.MMKV
import com.greendoyle.aihelpmegetjob.aitools.ToolRegistry
import com.greendoyle.aihelpmegetjob.aitools.CurrentTimeTool

class AiHelpMeGetJobApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)

        // 初始化工具注册中心
        initializeTools()
    }

    private fun initializeTools() {
        ToolRegistry.register(CurrentTimeTool())
    }
}
