package com.julianfortune.glacier.data

import java.time.LocalDate

data class Delivery (
    val receivedDate: LocalDate,
    val supplierId: Long?,
    val taxesCents: Long?,
    val feesCents: Long?,
)