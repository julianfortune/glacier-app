package com.julianfortune.glacier.feature.delivery.common.data

import com.julianfortune.glacier.ui.common.data.FormFieldState

data class EntryFormState(
    val selectedItemId: FormFieldState<Long?> = FormFieldState(null),
    val unitWeightPounds: FormFieldState<String> = FormFieldState(""),
    val unitWeightOunces: FormFieldState<String> = FormFieldState(""),
    val costStatusIsNoCost: FormFieldState<Boolean> = FormFieldState(false),
    val unitCost: FormFieldState<String> = FormFieldState(""),
    val unitCount: FormFieldState<String> = FormFieldState(""),
    // TODO(!!): Add program id and purchasing account id
    val isValid: Boolean = false,
)
