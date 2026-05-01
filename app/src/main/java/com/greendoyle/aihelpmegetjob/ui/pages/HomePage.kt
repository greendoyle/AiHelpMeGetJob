package com.greendoyle.aihelpmegetjob.ui.pages

//import android.content.Context
//import android.provider.Settings
//import android.text.TextUtils
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.greendoyle.aihelpmegetjob.permission.AccessibilityHolder
import com.greendoyle.aihelpmegetjob.permission.PermissionHelper
import com.greendoyle.aihelpmegetjob.utils.AppLauncher
import com.greendoyle.aihelpmegetjob.utils.RecruitApp
import com.greendoyle.aihelpmegetjob.utils.LogTool

@Composable
fun HomePage() {
    val context = LocalContext.current
    var taskStatus by remember { mutableStateOf("未启动") }
    var browsedCount by remember { mutableIntStateOf(0) }
    var greetingCount by remember { mutableIntStateOf(0) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    val TAG = "HomePage"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "任务状态", style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = taskStatus, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "已浏览: $browsedCount  |  已打招呼: $greetingCount")
            }
        }

        Button(
            onClick = {
                if (taskStatus == "运行中") {
                    taskStatus = "暂停"
                } else {
                    // 检查无障碍服务是否已启用
                    if (!AccessibilityHolder.isServiceAvailable) {
                        showAccessibilityDialog = true
                    } else {
                        taskStatus = "运行中"
                        // TODO: to start the agent task and open the target app
                        LogTool.d(TAG, "to start boss zhipin")
                        AppLauncher.launchRecruitApp(context, RecruitApp.BOSS_ZHI_PIN)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (taskStatus == "运行中") "暂停任务" else "启动任务")
        }
    }

    // 无障碍服务未启用提示对话框
    if (showAccessibilityDialog) {
        AccessibilityDialog(
            onDismiss = { showAccessibilityDialog = false },
            onOpenSettings = {
                PermissionHelper.openAccessibilitySettings(context)
                showAccessibilityDialog = false
            }
        )
    }
}

/**
 * 无障碍服务未启用提示对话框
 */
@Composable
fun AccessibilityDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "需要开启无障碍服务")
        },
        text = {
            Column {
                Text(
                    text = "本应用需要使用无障碍服务才能正常工作。请按照以下步骤开启：",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. 点击下方[设置]按钮，跳转到系统设置页面\n2. 在[已下载的应用]中找到[AiHelpMeGetJob]\n3. 打开[AiHelpMeGetJob]的无障碍服务开关",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenSettings
            ) {
                Text("设置")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("取消")
            }
        }
    )
}
