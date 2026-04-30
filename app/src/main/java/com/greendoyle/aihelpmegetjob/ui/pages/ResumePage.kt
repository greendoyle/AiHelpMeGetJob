package com.greendoyle.aihelpmegetjob.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greendoyle.aihelpmegetjob.data.model.ResumeInfo
import com.greendoyle.aihelpmegetjob.mmkv.StorageManager

@Composable
fun ResumePage() {
    val savedResume = remember { StorageManager.getResumeInfo() }
    var name by remember { mutableStateOf(savedResume.name) }
    var jobIntent by remember { mutableStateOf(savedResume.jobIntent) }
    var workYears by remember { mutableStateOf(savedResume.workYears) }
    var education by remember { mutableStateOf(savedResume.education) }
    var coreSkills by remember { mutableStateOf(savedResume.coreSkills) }
    var saveFeedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "简历信息", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("姓名") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = jobIntent,
            onValueChange = { jobIntent = it },
            label = { Text("求职意向") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("如：Java开发工程师") },
            singleLine = true
        )

        OutlinedTextField(
            value = workYears,
            onValueChange = { workYears = it },
            label = { Text("工作年限") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("如：3年") },
            singleLine = true
        )

        OutlinedTextField(
            value = education,
            onValueChange = { education = it },
            label = { Text("学历") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("如：本科") },
            singleLine = true
        )

        OutlinedTextField(
            value = coreSkills,
            onValueChange = { coreSkills = it },
            label = { Text("核心技能") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("如：Java, SpringBoot, MySQL") },
            maxLines = 3
        )

        Button(
            onClick = {
                StorageManager.saveResumeInfo(
                    ResumeInfo(name, jobIntent, workYears, education, coreSkills)
                )
                saveFeedback = "保存成功"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存简历")
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
