package com.julianfortune.glacier.data.domain.delivery

import java.time.LocalDate

data class DeliveryHeadline (
    val receivedDate: LocalDate,
    val supplierId: Long?,
    val taxesCents: Long?,
    val feesCents: Long?,
)