package com.greendoyle.aihelpmegetjob.permission

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import com.greendoyle.aihelpmegetjob.R
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern

object FloatWindowManager {

    private var mLogTextView: TextView? = null
    private var mScrollView: ScrollView? = null
    private var isInitialized = false

    const val TAG = "FloatWindow"

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        EasyFloat.with(context)
            .setTag("float_panel")
            .setLayout(R.layout.float_window_layout) { view ->
                mScrollView = view.findViewById(R.id.sv_log)
                mLogTextView = view.findViewById(R.id.tv_log)

                mScrollView?.post {
                    mScrollView?.fullScroll(View.FOCUS_DOWN)
                }

                view.findViewById<Button>(R.id.btn_record)?.setOnClickListener {
                    appendLog("点击按钮测试\n")
                }
            }
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RIGHT)
            .setDragEnable(true)
            .show()
    }

    fun appendLog(text: String) {
        mLogTextView?.append(text)
        mScrollView?.post {
            mScrollView?.fullScroll(View.FOCUS_DOWN)        }
    }
}
