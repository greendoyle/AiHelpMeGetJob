package com.greendoyle.aihelpmegetjob.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AiSettingsPage() {
    var apiUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "AI 模型设置", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = apiUrl,
            onValueChange = { apiUrl = it },
            label = { Text("API URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO: 测试API */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("测试")
            }
            Button(
                onClick = { /* TODO: 保存配置 */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("保存")
            }
            OutlinedButton(
                onClick = {
                    apiUrl = ""
                    apiKey = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("重置")
            }
        }
    }
}
