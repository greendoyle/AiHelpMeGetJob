package com.greendoyle.aihelpmegetjob.ui.navigation

sealed class Screen(
    val route: String,
    val title: String
) {
    object Home : Screen("home", "首页")
    object Settings : Screen("settings", "参数配置")
    object AiSettings : Screen("ai_settings", "AI设置")
    object Resume : Screen("resume", "简历")
    object History : Screen("history", "历史")
}

val BottomNavItems: List<Screen> = listOf(
    Screen.Home,
    Screen.Settings,
    Screen.AiSettings,
    Screen.Resume,
    Screen.History
)
