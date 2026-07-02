package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.common.EntityMetadata
import java.time.LocalDate

data class Delivery(
    override val id: Long,
    val received: LocalDate,
    val supplier: Supplier?,
    val taxesCents: Long?,
    val feesCents: Long?,
    val entries: List<Entry>,
    val metadata: EntityMetadata,
) : Entity {
    data class Entry(
        override val id: Long,
        val item: ItemHeadline,
        val unitCount: Long,
        val unitWeight: Weight,
        val costStatus: CostStatus,
        val unitCostCents: Long,
        val itemWeight: Weight?,
        val itemsPerUnit: Long?,
        val program: Program?,
        val purchasingAccount: PurchasingAccount?,
        val metadata: EntityMetadata,
    ) : Entity
}
