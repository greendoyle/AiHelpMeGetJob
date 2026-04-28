package com.greendoyle.aihelpmegetjob.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage() {
    var timeFilter by remember { mutableStateOf("今日") }
    var scoreFilter by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "历史记录", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var timeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = timeExpanded,
                onExpandedChange = { timeExpanded = !timeExpanded }
            ) {
                OutlinedTextField(
                    value = timeFilter,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("时间筛选") },
                    modifier = Modifier.weight(1f).menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = timeExpanded,
                    onDismissRequest = { timeExpanded = false }
                ) {
                    listOf("今日", "昨日", "近7天").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                timeFilter = option
                                timeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = scoreFilter,
                onValueChange = { scoreFilter = it },
                label = { Text("最低分数") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无历史记录",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
