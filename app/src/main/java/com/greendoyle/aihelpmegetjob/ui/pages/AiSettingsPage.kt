package com.greendoyle.aihelpmegetjob.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.greendoyle.aihelpmegetjob.data.model.AiConfig
import com.greendoyle.aihelpmegetjob.mmkv.StorageManager
import com.greendoyle.aihelpmegetjob.network.ApiClient

@Composable
fun AiSettingsPage() {
    val savedConfig = remember { StorageManager.getAiConfig() }
    var apiUrl by remember { mutableStateOf(savedConfig.apiUri) }
    var apiKey by remember { mutableStateOf(savedConfig.apiKey) }
    var saveFeedback by remember { mutableStateOf("") }

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
                onClick = {
                    StorageManager.saveAiConfig(AiConfig(apiUrl, apiKey))
                    saveFeedback = "保存成功"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("保存")
            }
            OutlinedButton(
                onClick = {
                    apiUrl = ""
                    apiKey = ""
                    saveFeedback = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("重置")
            }
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
