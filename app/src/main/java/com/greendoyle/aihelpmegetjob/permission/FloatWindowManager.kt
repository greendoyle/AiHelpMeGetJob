package com.greendoyle.aihelpmegetjob.permission

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import com.greendoyle.aihelpmegetjob.R
import com.greendoyle.aihelpmegetjob.agent.Agent
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object FloatWindowManager {

    private val coroutineScope = MainScope()
    private var isAnalyzing = false

    private var mLogTextView: TextView? = null
    private var mScrollView: ScrollView? = null
    private var isInitialized = false

    // 当前岗位卡片文本（由 BossZhiPinPageMonitor 写入）
    var currentJobCardText: String? = null

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
                    handleMatchButtonClick()
                }

                view.findViewById<Button>(R.id.btn_scroll_up)?.setOnClickListener {
                    mScrollView?.smoothScrollBy(0, -150)
                }

                view.findViewById<Button>(R.id.btn_scroll_down)?.setOnClickListener {
                    mScrollView?.smoothScrollBy(0, 150)
                }
            }
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RIGHT)
            .setDragEnable(true)
            .show()
    }

    fun destroy() {
        EasyFloat.dismiss("float_panel")
        (coroutineScope.coroutineContext[Job])?.cancel()
        mLogTextView = null
        mScrollView = null
        currentJobCardText = null
        isInitialized = false
    }

    fun appendLog(text: String) {
        mLogTextView?.append(text)
        mScrollView?.post {
            mScrollView?.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun handleMatchButtonClick() {
        if (isAnalyzing) return // 防止重复点击

        val jobText = currentJobCardText
        if (jobText.isNullOrEmpty()) {
            appendLog("\n暂无岗位数据，请先浏览职位详情页\n")
            return
        }

        appendLog("\n岗位匹配中...\n")
        isAnalyzing = true

        coroutineScope.launch {
            try {
                val result = Agent.analyze(jobText)
                appendLog("匹配结果：$result\n")
            } finally {
                isAnalyzing = false // 无论成功失败，都恢复状态
            }
        }
    }
}
