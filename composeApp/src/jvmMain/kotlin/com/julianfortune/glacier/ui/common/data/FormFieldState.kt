package com.julianfortune.glacier.ui.common.data

data class FormFieldState<Value>(
    val value: Value,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
)