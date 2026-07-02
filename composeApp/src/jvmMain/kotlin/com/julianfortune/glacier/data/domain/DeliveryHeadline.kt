package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.common.EntityMetadata
import java.time.LocalDate

data class DeliveryHeadline(
    override val id: Long,
    val received: LocalDate,
    val supplier: Supplier?,
    val taxesCents: Long?,
    val feesCents: Long?,
    val metadata: EntityMetadata,
) : Entity
