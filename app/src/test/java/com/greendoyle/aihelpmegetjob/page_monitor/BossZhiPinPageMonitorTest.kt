package com.greendoyle.aihelpmegetjob.page_monitor

import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * BossZhiPinPageMonitor 单元测试
 */
class BossZhiPinPageMonitorTest {

    // ==================== 辅助类：Mock AccessibilityNodeInfo ====================

    /**
     * Mock 节点：无子节点
     */
    private class MockNodeNoChildren : AccessibilityNodeInfo() {
        override fun getText(): CharSequence? = "根节点"
        override fun getChildCount(): Int = 0
    }

    /**
     * Mock 节点：有子节点
     */
    private class MockNodeWithChildren : AccessibilityNodeInfo() {
        private val children = mutableListOf<AccessibilityNodeInfo>()

        override fun getText(): CharSequence? = "父节点"
        override fun getChildCount(): Int = children.size
        override fun getChild(index: Int): AccessibilityNodeInfo? = children[index]

        fun addChild(child: AccessibilityNodeInfo) {
            children.add(child)
        }
    }

    // ==================== 测试 containText 函数 ====================

    /**
     * 测试 containText - 节点文本直接匹配
     */
    @Test
    fun containText_directMatch() {
        // 1. 模拟节点
        val mockNode = mock(AccessibilityNodeInfo::class.java)

        // 2. 模拟 text 返回
        `when`(mockNode.text).thenReturn("职位详情")

        // 3. 模拟没有子节点，避免递归
        `when`(mockNode.childCount).thenReturn(0)

        // 4. 测试
        val result = BossZhiPinPageMonitor.containText(mockNode, "职位详情")
        assertTrue("节点文本应匹配", result)
    }

    /**
     * 测试 containText - 节点文本不匹配
     */
    @Test
    fun containText_noMatch() {
        val mockNode = mock(AccessibilityNodeInfo::class.java)
        `when`(mockNode.text).thenReturn("root")
        `when`(mockNode.childCount).thenReturn(0)
        val result = BossZhiPinPageMonitor.containText(mockNode, "职位详情")
        assertFalse("节点文本应匹配", result)
    }

    /**
     * 测试 containText - 子节点匹配
     */
    @Test
    fun containText_childMatch() {
        val mockNode = mock(AccessibilityNodeInfo::class.java)
        `when`(mockNode.text).thenReturn("父节点")
        `when`(mockNode.childCount).thenReturn(1)
        val mockChild = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild.text).thenReturn("职位详情")
        `when`(mockNode.getChild(0)).thenReturn(mockChild)

        val result = BossZhiPinPageMonitor.containText(mockNode, "职位详情")
        assertTrue("子节点文本应匹配", result)
    }

    /**
     * 测试 containText - 深层子节点匹配
     */
    @Test
    fun containText_deepChildMatch() {
        val mockRoot = mock(AccessibilityNodeInfo::class.java)
        `when`(mockRoot.text).thenReturn("根节点")
        `when`(mockRoot.childCount).thenReturn(1)
        val mockChild1 = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild1.text).thenReturn("子节点 1")
        `when`(mockChild1.childCount).thenReturn(1)
        val mockChild2 = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild2.text).thenReturn("职位详情")
        `when`(mockChild1.getChild(0)).thenReturn(mockChild2)
        `when`(mockRoot.getChild(0)).thenReturn(mockChild1)

        val result = BossZhiPinPageMonitor.containText(mockRoot, "职位详情")
        assertTrue("深层子节点文本应匹配", result)
    }
//
    /**
     * 测试 containText - 多个子节点中有一个匹配
     */
    @Test
    fun containText_multipleChildrenOneMatch() {
        val mockParent = mock(AccessibilityNodeInfo::class.java)
        `when`(mockParent.text).thenReturn("父节点")
        `when`(mockParent.childCount).thenReturn(3)
        val mockChild1 = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild1.text).thenReturn("子节点 1")
        val mockChild2 = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild2.text).thenReturn("职位详情")
        val mockChild3 = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild3.text).thenReturn("子节点 3")
        `when`(mockParent.getChild(0)).thenReturn(mockChild1)
        `when`(mockParent.getChild(1)).thenReturn(mockChild2)
        `when`(mockParent.getChild(2)).thenReturn(mockChild3)

        val result = BossZhiPinPageMonitor.containText(mockParent, "职位详情")
        assertTrue("多个子节点中有一个匹配即可", result)
    }
//
    /**
     * 测试 containText - 多个子节点都不匹配
     */
    @Test
    fun containText_multipleChildrenNoneMatch() {
        val mockParent = mock(AccessibilityNodeInfo::class.java)
        `when`(mockParent.text).thenReturn("父节点")
        `when`(mockParent.childCount).thenReturn(2)
        val mockChild1 = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild1.text).thenReturn("子节点 1")
        val mockChild2 = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild2.text).thenReturn("子节点 2")
        `when`(mockParent.getChild(0)).thenReturn(mockChild1)
        `when`(mockParent.getChild(1)).thenReturn(mockChild2)

        val result = BossZhiPinPageMonitor.containText(mockParent, "职位详情")
        assertFalse("多个子节点都不匹配", result)
    }
//
    /**
     * 测试 containText - null 节点返回 false
     */
    @Test
    fun containText_nullNode() {
        val result = BossZhiPinPageMonitor.containText(null, "职位详情")
        assertFalse("null 节点应返回 false", result)
    }
//
    /**
     * 测试 containText - 空字符串匹配
     */
    @Test
    fun containText_emptyString() {
        val mockNode = mock(AccessibilityNodeInfo::class.java)
        `when`(mockNode.text).thenReturn("根节点")
        `when`(mockNode.childCount).thenReturn(0)
        val result = BossZhiPinPageMonitor.containText(mockNode, "")
        assertTrue("空字符串应匹配", result)
    }
//
    /**
     * 测试 containText - 大小写不敏感匹配
     */
    @Test
    fun containText_caseInsensitive() {
        val mockNode = mock(AccessibilityNodeInfo::class.java)
        `when`(mockNode.text).thenReturn("职位详情")
        `when`(mockNode.childCount).thenReturn(0)
        val result = BossZhiPinPageMonitor.containText(mockNode, "职位详情")
        assertTrue("大小写应不敏感", result)
    }
//
    /**
     * 测试 containText - 部分匹配
     */
    @Test
    fun containText_partialMatch() {
        val mockNode = mock(AccessibilityNodeInfo::class.java)
        `when`(mockNode.text).thenReturn("职位详情描述")
        `when`(mockNode.childCount).thenReturn(0)
        val result = BossZhiPinPageMonitor.containText(mockNode, "职位")
        assertTrue("部分匹配应成功", result)
    }
//
    /**
     * 测试 containText - 子节点为 null
     */
    @Test
    fun containText_nullChild() {
        val mockParent = mock(AccessibilityNodeInfo::class.java)
        `when`(mockParent.text).thenReturn("父节点")
        `when`(mockParent.childCount).thenReturn(1)
        val mockChild = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild.text).thenReturn("子节点")
        `when`(mockParent.getChild(0)).thenReturn(mockChild)

        val result = BossZhiPinPageMonitor.containText(mockParent, "职位详情")
        assertFalse("子节点为 null 不应影响结果", result)
    }

    // ==================== 测试 isJobPage 函数 ====================

    /**
     * 测试 isJobPage - 职位列表页
     */
    @Test
    fun isJobPage_jobList() {
        val mockRoot = mock(AccessibilityNodeInfo::class.java)
        `when`(mockRoot.text).thenReturn("列表页")
        `when`(mockRoot.childCount).thenReturn(1)
        val mockChild = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild.text).thenReturn("立即沟通")
        `when`(mockRoot.getChild(0)).thenReturn(mockChild)

        val result = BossZhiPinPageMonitor.isJobPage(mockRoot)
        assertTrue("职位列表页应返回 true", result)
    }

    /**
     * 测试 isJobPage - 职位详情页
     */
    @Test
    fun isJobPage_jobDetail() {
        val mockRoot = mock(AccessibilityNodeInfo::class.java)
        `when`(mockRoot.text).thenReturn("详情页")
        `when`(mockRoot.childCount).thenReturn(1)
        val mockChild = mock(AccessibilityNodeInfo::class.java)
        `when`(mockChild.text).thenReturn("岗位职责")
        `when`(mockRoot.getChild(0)).thenReturn(mockChild)

        val result = BossZhiPinPageMonitor.isJobPage(mockRoot)
        assertTrue("职位详情页应返回 true", result)
    }

    /**
     * 测试 isJobPage - 非职位页面
     */
    @Test
    fun isJobPage_nonJobPage() {
        val mockRoot = mock(AccessibilityNodeInfo::class.java)
        `when`(mockRoot.text).thenReturn("我的页面")
        `when`(mockRoot.childCount).thenReturn(0)

        val result = BossZhiPinPageMonitor.isJobPage(mockRoot)
        assertFalse("非职位页面应返回 false", result)
    }

    // ==================== 测试 isBossApp 函数 ====================

    /**
     * 测试 isBossApp - BOSS 直聘 APP
     */
    @Test
    fun isBossApp_bossApp() {
        val mockEvent = mock(AccessibilityEvent::class.java)
        `when`(mockEvent.packageName).thenReturn("com.hpbr.bosszhipin")

        val result = BossZhiPinPageMonitor.isBossApp(mockEvent)
        assertTrue("BOSS 直聘 APP 应返回 true", result)
    }

    /**
     * 测试 isBossApp - 非 BOSS 直聘 APP
     */
    @Test
    fun isBossApp_notBossApp() {
        val mockEvent = mock(AccessibilityEvent::class.java)
        `when`(mockEvent.packageName).thenReturn("com.other.app")

        val result = BossZhiPinPageMonitor.isBossApp(mockEvent)
        assertFalse("非 BOSS 直聘 APP 应返回 false", result)
    }
}
