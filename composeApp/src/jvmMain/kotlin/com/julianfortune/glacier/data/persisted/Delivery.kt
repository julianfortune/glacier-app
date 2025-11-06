package com.julianfortune.glacier.data.persisted

import java.time.Instant
import java.time.LocalDate

data class Delivery (
    override val id: Long,

    // NOTE: Worry about this later
    // val createdAt: Instant,
    // val updatedAt: Instant,

    // Data
    val receivedDate: LocalDate,
    val supplierId: Long?,
    val taxesCents: Long?,
    val feesCents: Long?,

    // Foreign relationships
    val entries: List<Entry>,
) : Persisted
