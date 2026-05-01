package com.greendoyle.aihelpmegetjob.utils
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

object UiTreeTraverser {

    private const val TAG = "UiTreeTraverser"

    /**
     * 遍历整棵 UI 树，打印：层级 + 父路径 + 控件名 + 文字 + 资源ID
     * @param root 根节点 rootInActiveWindow
     */
    fun traverseTree(root: AccessibilityNodeInfo?) {
        root ?: return
        traverseRecursive(
            node = root,
            currentDepth = 0,
            parentPath = "Root"
        )
    }

    /**
     * 递归遍历
     * @param node 当前节点
     * @param currentDepth 当前层级（0 = 根）
     * @param parentPath 父节点路径（用于拼接完整位置）
     */
    private fun traverseRecursive(
        node: AccessibilityNodeInfo?,
        currentDepth: Int,
        parentPath: String
    ) {
        node ?: return

        try {
            // 1. 获取当前节点信息
            val className = node.className?.toString() ?: "UnknownView"
            val text = node.text?.toString() ?: ""
            val resId = node.viewIdResourceName ?: ""
            val currentPath = "$parentPath > $className"

            // 2. 拼接输出日志（你要的结构）
            val logStr = buildString {
                append("【层级 $currentDepth】")
                append(" | 路径: $currentPath")
                append(" | 文字: [$text]")
                if (resId.isNotEmpty()) {
                    append(" | ID: $resId")
                }
            }
            Log.d(TAG, logStr)

            // 3. 递归所有子节点
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                traverseRecursive(
                    node = child,
                    currentDepth = currentDepth + 1,
                    parentPath = currentPath
                )
            }

        } catch (e: Exception) {
            // 防止系统节点异常崩溃
            Log.e(TAG, "遍历异常", e)
        } finally {
            // 必须回收，非常重要
            node.recycle()
        }
    }
}