package com.julianfortune.glacier.view

import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

@Composable
fun CurrencyInputTextField(
    valueCents: Long?,
    onValueChange: (Long?) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    onFocusLost: () -> Unit = {}
) {
    OutlinedTextField(
        value = if (valueCents != null) "$${valueCents/100}" else "$",
        onValueChange = {
            println(it)
        },
        label = { Text("Cost in Cents") },
        modifier = Modifier
            .height(64.dp)
            .onFocusChanged({ state ->
                if (!state.isFocused) {
                    // Check for error
                }
            }),
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(),
        enabled = enabled,
    )
}