package com.greendoyle.aihelpmegetjob.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.greendoyle.aihelpmegetjob.ui.pages.AiSettingsPage
import com.greendoyle.aihelpmegetjob.ui.pages.HistoryPage
import com.greendoyle.aihelpmegetjob.ui.pages.HomePage
import com.greendoyle.aihelpmegetjob.ui.pages.ResumePage
import com.greendoyle.aihelpmegetjob.ui.pages.SettingsPage

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { HomePage() }
        composable(Screen.Settings.route) { SettingsPage() }
        composable(Screen.AiSettings.route) { AiSettingsPage() }
        composable(Screen.Resume.route) { ResumePage() }
        composable(Screen.History.route) { HistoryPage() }
    }
}
