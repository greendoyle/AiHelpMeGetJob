package com.greendoyle.aihelpmegetjob.data.model

data class FilterConfig(
    val keywords: String = "",
    val city: String = "",
    val salaryMin: String = "",
    val salaryMax: String = "",
    val acceptOutsourcing: String = "",
    val operateInterval: String = "",
    val dailyLimit: String = ""
)
