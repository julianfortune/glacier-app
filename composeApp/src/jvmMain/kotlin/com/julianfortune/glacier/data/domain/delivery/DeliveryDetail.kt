package com.julianfortune.glacier.data.domain.delivery

import com.julianfortune.glacier.data.domain.entry.Entry
import java.time.LocalDate

data class DeliveryDetail(
    val receivedDate: LocalDate,
    val supplierId: Long?,
    val taxesCents: Long?,
    val feesCents: Long?,
    val entries: List<Entry>?,
)
