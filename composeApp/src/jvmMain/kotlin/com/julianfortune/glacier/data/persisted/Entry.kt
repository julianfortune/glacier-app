package com.julianfortune.glacier.data.persisted

import com.julianfortune.glacier.data.CostStatus
import com.julianfortune.glacier.data.DeliveryEntryAggregation

data class Entry(
    override val id: Long,

    // Data
    val itemCount: Long,
    val costStatus: CostStatus,
    val itemCostCents: Long,
    val aggregate: DeliveryEntryAggregation?,

    // Foreign relationships (requiring hydration)
    val item: Item,
    val purchasingAccounts: List<PurchasingAccountAllocation>,
    val programs: List<ProgramAllocation>
) : Persisted
