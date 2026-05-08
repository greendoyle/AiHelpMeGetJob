package com.greendoyle.aihelpmegetjob.ui

import com.greendoyle.aihelpmegetjob.R
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView


object FloatWindowManager {
    private var windowManager: WindowManager? = null
    private var floatView: android.view.View? = null
    private var tvContent: TextView? = null

    // 初始化悬浮窗
    fun init(context: Context) {
        if (floatView != null) return

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        // 加载悬浮窗布局
        floatView = LayoutInflater.from(context).inflate(R.layout.float_window_layout, null)
        tvContent = floatView?.findViewById(R.id.tv_content)

        // 透明窗口参数
        val params = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.START or Gravity.TOP
            x = 100
            y = 300
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.TRANSLUCENT // 透明
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        }

        windowManager?.addView(floatView, params)
    }

    // 更新悬浮窗文字（API返回结果）
    fun updateWindowText(content: String) {
        tvContent?.text = content
    }

    // 关闭悬浮窗
    fun dismiss() {
        floatView?.let { windowManager?.removeView(it) }
        floatView = null
    }
}