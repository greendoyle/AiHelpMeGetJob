package com.greendoyle.aihelpmegetjob

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
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
import com.greendoyle.aihelpmegetjob.permission.AccessibilityHolder

import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 应用重启后从系统设置恢复无障碍权限状态，避免重复申请
        AccessibilityHolder.syncFromSystem(this)

        // ========== 初始化全局悬浮面板 ==========
        initGlobalFloatView()

        setContent {
            AiHelpMeGetJobTheme {
                MainScreen()
            }
        }
    }

    private lateinit var mLogTextView: TextView
    private lateinit var mScrollView: ScrollView

    private fun initGlobalFloatView() {

        //创建【展开面板】（默认隐藏）
        EasyFloat.with(this)
            .setTag("float_panel") // 唯一标识，和球区分
            .setLayout(R.layout.float_window_layout) { view ->
                // 🔥 第一步：先初始化TextView（必须放在点击事件前面！）
                mScrollView = view.findViewById(R.id.sv_log)
                mLogTextView = view.findViewById(R.id.tv_log)
                // 初始化文字
                mLogTextView.append("测试一下：\n")
                // 初始化后滚动到底部
                mScrollView.post {
                    // 🔥 正确调用：ScrollView的fullScroll方法
                    mScrollView.fullScroll(View.FOCUS_DOWN)
                }

                // 🔥 第二步：再初始化按钮并设置点击事件
                view.findViewById<Button>(R.id.btn_record).setOnClickListener {
                    // 追加日志
                    mLogTextView.append("测试识别结果：${System.currentTimeMillis()}\n")
                    mScrollView.post {
                        mScrollView.fullScroll(View.FOCUS_DOWN)
                    }
                }
            }
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RIGHT)
            .setDragEnable(true)
            .show()

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
