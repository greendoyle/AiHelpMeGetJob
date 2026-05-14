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
    var salaryMin by remember { mutableStateOf(savedFilter.salaryMin) }
    var salaryMax by remember { mutableStateOf(savedFilter.salaryMax) }
    var outsourcing by remember {
        mutableStateOf(savedFilter.acceptOutsourcing.ifEmpty { "不限" })
    }
    var interval by remember {
        mutableStateOf(savedFilter.operateInterval.takeIf { it.isNotBlank() } ?:"30")
    }
    var dailyLimit by remember {
        mutableStateOf(savedFilter.dailyLimit.takeIf { it.isNotBlank() } ?: "50")
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = salaryMin,
                onValueChange = { salaryMin = it },
                label = { Text("最低薪资") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = salaryMax,
                onValueChange = { salaryMax = it },
                label = { Text("最高薪资") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
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
            value = interval,
            onValueChange = {
                if (it.isEmpty() || it.all(Char::isDigit)) {
                    interval = it
                }
            },
            label = { Text("操作间隔 (秒)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = dailyLimit,
            onValueChange = {
                if (it.isEmpty() || it.all(Char::isDigit)) {
                    dailyLimit = it
                }
            },
            label = { Text("每日最大打招呼次数") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                StorageManager.saveFilterConfig(
                    FilterConfig(
                        keywords = keywords,
                        city = city,
                        salaryMin = salaryMin,
                        salaryMax = salaryMax,
                        acceptOutsourcing = outsourcing,
                        operateInterval = interval.ifBlank { "30" },
                        dailyLimit = dailyLimit.ifBlank { "50" }
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
