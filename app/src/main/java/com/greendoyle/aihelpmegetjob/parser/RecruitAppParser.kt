package com.greendoyle.aihelpmegetjob.parser

import android.view.accessibility.AccessibilityNodeInfo
import com.greendoyle.aihelpmegetjob.data.model.RecruitJob

interface RecruitAppParser {
    // 解析职位列表页
    fun parseJobList(rootNode: AccessibilityNodeInfo): List<RecruitJob>
    // 解析职位详情页
    fun parseJobDetail(rootNode: AccessibilityNodeInfo): RecruitJob?

    fun parse(rootNode: AccessibilityNodeInfo)
}