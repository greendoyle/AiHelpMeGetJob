package com.greendoyle.aihelpmegetjob.page_monitor

import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.greendoyle.aihelpmegetjob.agent.Agent
import com.greendoyle.aihelpmegetjob.permission.FloatWindowManager
import com.greendoyle.aihelpmegetjob.utils.LogTool
import com.greendoyle.aihelpmegetjob.utils.UiTreeTraverser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

object BossZhiPinPageMonitor {

    private const val BOSS_PACKAGE = "com.hpbr.bosszhipin"
    private const val TAG = "BossZhiPinPageMonitor"
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())
    private var checkTask: Runnable? = null
    /**
     * 判断是不是 BOSS 直聘 APP
     */
    fun isBossApp(event: AccessibilityEvent): Boolean {
        return event.packageName.toString() == BOSS_PACKAGE
    }

    /**
     * 判断当前页面是不是【职位相关页面】
     * 列表页 or 详情页 → return true
     * 其他页（我的、消息、设置）→ return false
     */
    fun isJobPage(rootNode: AccessibilityNodeInfo): Boolean {
        return hasJobDetailFeature(rootNode)
    }

    /**
     * 判断是不是职位详情页
     * 特征：职位详情、岗位职责、任职要求
     */
    private fun hasJobDetailFeature(rootNode: AccessibilityNodeInfo): Boolean {
        return containText(rootNode, "职位详情") || containText(rootNode, "立即沟通")
    }

    /**
     * 工具：递归遍历所有节点，判断是否包含指定文字
     */
    fun containText(node: AccessibilityNodeInfo?, target: String): Boolean {
        if (node == null || target.isBlank()) return false

        // 清洗目标文本：去除零宽字符、多余空格、转小写
        val cleanTarget = target.replace(Regex("[\\s\\u200B\\uFEFF]"), "").lowercase()

        // 优先使用系统高效 API
        val systemMatch = node.findAccessibilityNodeInfosByText(cleanTarget).isNotEmpty()
        if (systemMatch) return true

        // 降级方案：手动递归（针对系统 API 漏检的自定义 View）
        return checkNodeRecursively(node, cleanTarget)
    }

    private fun checkNodeRecursively(node: AccessibilityNodeInfo, target: String): Boolean {
        val text = node.text?.toString()?.replace(Regex("[\\s\\u200B\\uFEFF]"), "")?.lowercase()
        val desc = node.contentDescription?.toString()?.replace(Regex("[\\s\\u200B\\uFEFF]"), "")?.lowercase()

        if (text?.contains(target) == true || desc?.contains(target) == true) return true

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                if (checkNodeRecursively(child, target)) return true
            } finally {
                child.recycle()
            }
        }
        return false
    }
    fun onAccessibilityEvent(event: AccessibilityEvent, getRoot: () -> AccessibilityNodeInfo?) {
        checkTask?.let { handler.removeCallbacks(it) }
        val isTargetPage = isBossApp(event)
        if (!isTargetPage) {
            LogTool.d(TAG, "not boss detailed page")
            return
        }
        // 延迟 300ms 等待 View 树构建完成
        checkTask = Runnable {
            // ⭐ 关键：此时重新获取根节点，确保拿到渲染完成后的新树
            val root = getRoot() ?: return@Runnable
            try {
                val found = isJobPage(root)
                if (found) {
                    val jobText = UiTreeTraverser.collectLinearLayoutChildrenText(root)
                    FloatWindowManager.currentJobCardText = jobText
                    // FloatWindowManager.appendLog("\n$jobText")
                    // TODO: Agent逻辑
//                    serviceScope.launch()
//                    {
//                        val result = Agent.analyze(jobText)
//                        FloatWindowManager.appendLog(result)
//                    }
                }
            } finally {
                root.recycle() // 必须回收，防止内存泄漏
            }
        }
        handler.postDelayed(checkTask!!, 300)
    }
}