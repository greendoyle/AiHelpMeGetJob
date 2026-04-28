package com.greendoyle.aihelpmegetjob.data.model

data class HistoryRecord(
    val id: Long = 0,
    val jobName: String = "",
    val companyName: String = "",
    val salary: String = "",
    val matchScore: Int = 0,
    val greetingText: String = "",
    val timestamp: Long = 0,
    val sendStatus: String = ""
)
