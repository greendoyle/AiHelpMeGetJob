package com.greendoyle.aihelpmegetjob.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    var keywords by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var salaryRange by remember { mutableStateOf("") }
    var outsourcing by remember { mutableStateOf("不限") }
    var interval by remember { mutableIntStateOf(30) }
    var dailyLimit by remember { mutableIntStateOf(50) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "筛选条件", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = keywords,
            onValueChange = { keywords = it },
            label = { Text("岗位关键词") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("工作城市") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = salaryRange,
            onValueChange = { salaryRange = it },
            label = { Text("期望薪资 (如 5k-8k)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        var outsourcingExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = outsourcingExpanded,
            onExpandedChange = { outsourcingExpanded = !outsourcingExpanded }
        ) {
            OutlinedTextField(
                value = outsourcing,
                onValueChange = {},
                readOnly = true,
                label = { Text("外包选项") },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = outsourcingExpanded,
                onDismissRequest = { outsourcingExpanded = false }
            ) {
                listOf("接受外包", "不接受外包", "不限").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            outsourcing = option
                            outsourcingExpanded = false
                        }
                    )
                }
            }
        }

        HorizontalDivider()

        Text(text = "风控规则", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = interval.toString(),
            onValueChange = { interval = it.toIntOrNull() ?: 30 },
            label = { Text("操作间隔 (秒)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = dailyLimit.toString(),
            onValueChange = { dailyLimit = it.toIntOrNull() ?: 50 },
            label = { Text("每日最大打招呼次数") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
