package com.julianfortune.glacier.feature.delivery.common.data

import com.julianfortune.glacier.ui.common.data.FormFieldState

data class EntryFormState(
    val itemId: FormFieldState<Long?> = FormFieldState(null),
    val unitCount: FormFieldState<String> = FormFieldState(""),
    val unitWeight: FormFieldState<String> = FormFieldState(""),
    val isNoCost: FormFieldState<Boolean> = FormFieldState(false),
    val unitCost: FormFieldState<String> = FormFieldState(""),
    val isValid: Boolean = false,
)
