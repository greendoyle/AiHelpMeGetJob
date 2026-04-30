package com.greendoyle.aihelpmegetjob.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greendoyle.aihelpmegetjob.data.model.FilterConfig
import com.greendoyle.aihelpmegetjob.mmkv.StorageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    val savedFilter = remember { StorageManager.getFilterConfig() }
    var keywords by remember { mutableStateOf(savedFilter.keywords) }
    var city by remember { mutableStateOf(savedFilter.city) }
    var salaryRange by remember {
        val parts = listOf(savedFilter.salaryMin, savedFilter.salaryMax).filter { it.isNotEmpty() }
        mutableStateOf(parts.joinToString("-"))
    }
    var outsourcing by remember {
        mutableStateOf(savedFilter.acceptOutsourcing.ifEmpty { "不限" })
    }
    var interval by remember {
        mutableIntStateOf(savedFilter.operateInterval.toIntOrNull() ?: 30)
    }
    var dailyLimit by remember {
        mutableIntStateOf(savedFilter.dailyLimit.toIntOrNull() ?: 50)
    }
    var saveFeedback by remember { mutableStateOf("") }

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

        Button(
            onClick = {
                val parts = salaryRange.split("-")
                val salaryMin = parts.getOrNull(0)?.trim() ?: ""
                val salaryMax = parts.getOrNull(1)?.trim() ?: ""
                StorageManager.saveFilterConfig(
                    FilterConfig(
                        keywords = keywords,
                        city = city,
                        salaryMin = salaryMin,
                        salaryMax = salaryMax,
                        acceptOutsourcing = outsourcing,
                        operateInterval = interval.toString(),
                        dailyLimit = dailyLimit.toString()
                    )
                )
                saveFeedback = "保存成功"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存配置")
        }

        if (saveFeedback.isNotEmpty()) {
            Text(
                text = saveFeedback,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
