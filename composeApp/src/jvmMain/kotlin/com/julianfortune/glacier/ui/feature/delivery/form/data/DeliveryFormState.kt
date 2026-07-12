package com.julianfortune.glacier.ui.feature.delivery.form.data

import com.julianfortune.glacier.ui.common.LocalDateInput
import com.julianfortune.glacier.ui.common.data.FormFieldState

data class DeliveryFormState(
    val receivedDate: FormFieldState<String> = FormFieldState(LocalDateInput.ofToday().value),
    val selectedSupplierId: FormFieldState<Long?> = FormFieldState(null),
    val taxes: FormFieldState<String> = FormFieldState(""),
    val fees: FormFieldState<String> = FormFieldState(""),
    val isValid: Boolean = false,
)