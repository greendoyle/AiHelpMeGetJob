package com.greendoyle.aihelpmegetjob.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange

// 🔥 全局复用：获得焦点自动全选的 OutlinedTextField
@Composable
fun OutlinedTextFieldSelectAllOnFocus(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null
) {
    // 内部管理选中状态，默认不选中任何文本
    val textFieldValue = remember(value) {
        TextFieldValue(
            text = value,
            selection = TextRange(value.length) // 默认光标在末尾，不选中任何文本
        )
    }

    var currentState by remember { mutableStateOf(textFieldValue) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // 当外部 value 变化时更新内部状态
    LaunchedEffect(value) {
        if (currentState.text != value) {
            currentState = currentState.copy(text = value, selection = TextRange(value.length))
        }
    }

    // 焦点变化 → 自动全选
    LaunchedEffect(isFocused) {
        if (isFocused && currentState.selection.length != currentState.text.length) {
            currentState = currentState.copy(
                selection = TextRange(0, currentState.text.length)
            )
        }
    }

    OutlinedTextField(
        value = currentState,
        onValueChange = {
            currentState = it
            onValueChange(it.text) // 对外只暴露 String
        },
        label = label,
        modifier = modifier,
        singleLine = singleLine,
        interactionSource = interactionSource,
        placeholder = placeholder
    )
}