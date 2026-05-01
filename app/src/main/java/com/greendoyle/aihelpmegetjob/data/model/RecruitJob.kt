package com.greendoyle.aihelpmegetjob.data.model

import java.io.Serializable

/**
 * 招聘岗位统一数据模型
 * 适配 BOSS直聘 / 猎聘 / 智联 等所有平台
 */

data class RecruitJob(
    // 1. 基础信息
    val jobName: String = "",               // 职位名称：Java开发、产品经理
    val companyName: String = "",           // 公司名称
    val salary: String = "",                // 薪资：15-25K·13薪
    val cityArea: String = "",              // 城市/区域：北京·朝阳区

    // 2. 岗位要求
    val workYears: String = "",             // 工作经验：3-5年
    val education: String = "",              // 学历：本科
    val jobTags: List<String> = emptyList(),// 标签：五险一金、带薪年假、加班少

    // 3. 公司信息
    val companyType: String = "",           // 公司类型：互联网、上市公司
    val companySize: String = "",           // 公司规模：500-1000人

    // 4. 职位详情
    val jobRequirement: String = "",         // 岗位要求
    val jobResponsibility: String = "",      // 岗位职责

    // 5. 业务扩展字段
    val sourceApp: String = "",             // 来源：BOSS直聘、猎聘
    val isApplied: Boolean = false          // 是否已投递
): Serializable