package com.greendoyle.aihelpmegetjob.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomePage() {
    var taskStatus by remember { mutableStateOf("未启动") }
    var browsedCount by remember { mutableIntStateOf(0) }
    var greetingCount by remember { mutableIntStateOf(0) }

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
                taskStatus = if (taskStatus == "运行中") "暂停" else "运行中"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (taskStatus == "运行中") "暂停任务" else "启动任务")
        }
    }
}
