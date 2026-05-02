package com.greendoyle.aihelpmegetjob.utils
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

data class JobCard(
    val allText: String,       // 卡片所有文字（直接丢给AI）
    val cardNode: AccessibilityNodeInfo  // 卡片节点（用来点击）
)

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
     * 遍历整棵 UI 树，打印：层级 + 父路径 + 控件名 + 文字 + 资源ID
     * @param root 根节点 rootInActiveWindow
     */
    fun scanAllJobCards(root: AccessibilityNodeInfo?) {
        root ?: return
        val result = mutableListOf<JobCard>()
        findAllCardItems(root, result)
        val printContent = buildString {
        result.forEachIndexed { index, card ->
            append("======================================\n")
            append("找到第 ${index + 1} 张职位卡片\n")
            append("卡片内容：${card.allText}\n")
            append("======================================\n\n")
            }
        }
        Log.d(TAG, printContent)
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
            val childCount = node.childCount
            // 2. 拼接输出日志（你要的结构）
            val logStr = buildString {
                append("【层级 $currentDepth】")
                append(" | 路径: $currentPath")
                append(" | 文字: [$text]")
                append(" | childCount: $childCount")
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

    // 递归寻找所有卡片（RecyclerView 里的 item）
    private fun findAllCardItems(
        node: AccessibilityNodeInfo,
        outCards: MutableList<JobCard>
    ) {
        node ?: return
        try {
        // 判断：这是一个卡片（LinearLayout，且有多个子元素）
        if (node.className?.contains("ViewGroup") == true && node.childCount > 10) {
            val allText = collectAllTextInside(node)
            if (allText.isNotBlank() && allText.length > 20) {
                outCards.add(
                    JobCard(
                        allText = allText,
                        cardNode = node
                    )
                )
                return // 找到一张卡片，不再深入
            }
        }

        // 遍历子节点
        for (i in 0 until node.childCount) {
            findAllCardItems(node.getChild(i), outCards)
        }
        } catch (e: Exception) {
            // 防止系统节点异常崩溃
            Log.e(TAG, "遍历异常", e)
        } finally {
            // 必须回收，非常重要
            node.recycle()
        }
    }

    // 收集这个节点下所有 TextView 的文字
    private fun collectAllTextInside(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        val sb = StringBuilder()

        // 提取当前节点文字
        node.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let {
            sb.append(it).append("|")
        }

        // 递归提取子节点
        for (i in 0 until node.childCount) {
            sb.append(collectAllTextInside(node.getChild(i)))
        }

        return sb.toString().trim()
    }
}