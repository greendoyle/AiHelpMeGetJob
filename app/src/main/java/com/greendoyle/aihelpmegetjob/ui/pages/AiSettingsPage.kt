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
import com.greendoyle.aihelpmegetjob.ui.components.OutlinedTextFieldSelectAllOnFocus
 
import kotlinx.coroutines.launch

@Composable
fun AiSettingsPage() {
    val savedConfig = remember { StorageManager.getAiConfig() }
    var apiUrl by remember { mutableStateOf(savedConfig.apiUri) }
    var apiKey by remember { mutableStateOf(savedConfig.apiKey) }
    var model by remember { mutableStateOf(savedConfig.model) }
    var saveFeedback by remember { mutableStateOf("") }
    var testFeedback by remember { mutableStateOf("") }
    var isTesting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "AI 模型设置", style = MaterialTheme.typography.titleMedium)

        OutlinedTextFieldSelectAllOnFocus(
            value = apiUrl,
            onValueChange = { apiUrl = it },
            label = { Text("API URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextFieldSelectAllOnFocus(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextFieldSelectAllOnFocus(
            value = model,
            onValueChange = { model = it },
            label = { Text("Model") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("例如：gpt-3.5-turbo, claude-3-5-sonnet") }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    isTesting = true
                    testFeedback = ""
                    scope.launch {
                        ApiClient.setBaseUrl(apiUrl)
                        ApiClient.setApiKey(apiKey)
                        val result = ApiClient.testConnectivity(apiKey, model = model)
                        isTesting = false
                        testFeedback = if (result.isSuccess) {
                            "连通性测试成功！"
                        } else {
                            "连通性测试失败：${result.exceptionOrNull()?.message}"
                        }
                    }
                },
                enabled = !isTesting,
                modifier = Modifier.weight(1f)
            ) {
                if (isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("测试")
            }
            Button(
                onClick = {
                    StorageManager.saveAiConfig(AiConfig(apiUrl, apiKey, model))
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
                    model = ""
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
        if (testFeedback.isNotEmpty()) {
            Text(
                text = testFeedback,
                color = if (testFeedback.contains("成功")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
