package com.greendoyle.aihelpmegetjob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import com.greendoyle.aihelpmegetjob.ui.navigation.BottomNavItems
import com.greendoyle.aihelpmegetjob.ui.navigation.AppNavigation
import com.greendoyle.aihelpmegetjob.ui.navigation.Screen
import com.greendoyle.aihelpmegetjob.ui.theme.AiHelpMeGetJobTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiHelpMeGetJobTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val icons = mapOf(
        Screen.Home.route to Icons.Default.Home,
        Screen.Settings.route to Icons.Default.Settings,
        Screen.AiSettings.route to Icons.Default.Build,
        Screen.Resume.route to Icons.Default.Person,
        Screen.History.route to Icons.Default.History,
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icons[screen.route] ?: Icons.Default.Home,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AppNavigation(navController)
        }
    }
}
