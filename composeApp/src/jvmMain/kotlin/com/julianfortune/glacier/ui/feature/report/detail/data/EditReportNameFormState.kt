package com.julianfortune.glacier.ui.feature.report.detail.data

import com.julianfortune.glacier.ui.common.data.FormFieldState

data class EditReportNameFormState(
    val name: FormFieldState<String> = FormFieldState(""),
    val isValid: Boolean = false,
)
