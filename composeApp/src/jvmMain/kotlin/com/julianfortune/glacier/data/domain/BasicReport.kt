package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.common.EntityMetadata
import java.time.LocalDate

data class BasicReport(
    override val id: Long,
    val name: String,
    val criteria: Criteria,
    val metadata: EntityMetadata,
) : Entity {
    data class Criteria(
        val start: LocalDate,
        val end: LocalDate,
        val item: ItemHeadline?,
        val category: Category?,
        val costStatus: CostStatus?,
        val program: Program?,
        val purchasingAccount: PurchasingAccount?,
        val supplier: Supplier?,
    )
}
