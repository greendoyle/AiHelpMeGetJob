package com.greendoyle.aihelpmegetjob.permission

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
//import com.greendoyle.aihelpmegetjob.permission.AccessibilityHolder

// 悬浮窗 Service
@SuppressLint("AccessibilityPolicy")
class AlterWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatView: View? = null

    override fun onCreate() {
        super.onCreate()
        initFloatWindow()
    }

    private fun initFloatWindow() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val textView = TextView(this).apply {
            text = "悬浮球"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            setPadding(40, 40, 40, 40)
            setBackgroundResource(android.R.color.darker_gray)
        }
        floatView = textView

        // 点击事件
        floatView?.setOnClickListener {
            if (AccessibilityHolder.isServiceAvailable) {
                Toast.makeText(this, "开始执行任务", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "请先开启无障碍", Toast.LENGTH_SHORT).show()
            }
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.LEFT
            x = 100
            y = 300
        }

        windowManager.addView(floatView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        floatView?.let { windowManager.removeView(it) }
        floatView = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}