package com.julianfortune.glacier.feature.delivery.editor.data

import com.julianfortune.glacier.ui.common.CurrencyInput
import com.julianfortune.glacier.ui.common.LocalDateInput
import com.julianfortune.glacier.ui.common.data.Option
import java.time.LocalDate

data class DeliveryEditorState(
    val suppliers: List<Option<Long>> = emptyList(),
    val receivedDate: LocalDateInput? = LocalDateInput.ofToday(),
    val receivedDateError: Boolean = false,
    val selectedSupplier: Option<Long>? = null,
    val fees: CurrencyInput? = null,
    val taxes: CurrencyInput? = null,
    val validated: Validated? = null,
) {
    data class Validated(
        val supplierId: Long,
        val receivedDate: LocalDate,
        val taxesCents: Long? = null,
        val feesCents: Long? = null,
    )
}
