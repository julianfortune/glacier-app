package com.julianfortune.glacier.ui.feature.savedweight.form.data

import com.julianfortune.glacier.ui.common.data.FormFieldState

data class SavedWeightFormState(
    val lbs: FormFieldState<String> = FormFieldState(""),
    val oz: FormFieldState<String> = FormFieldState(""),
    val isValid: Boolean = false,
)
