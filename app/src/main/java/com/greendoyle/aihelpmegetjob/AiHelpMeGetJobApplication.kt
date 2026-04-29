package com.greendoyle.aihelpmegetjob

import android.app.Application
import com.tencent.mmkv.MMKV

class AiHelpMeGetJobApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}
