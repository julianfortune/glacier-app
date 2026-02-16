package com.julianfortune.glacier.data.domain.entry

data class Entry(
    val itemId: Long,
    val unitCount: Long,
    val unitName: String,
    val unitWeightGrams: Long,
    val itemsPerUnit: Long?,
    val costStatus: CostStatus,
    val unitCostCents: Long,
    val purchasingAccountAllocations: List<Allocation<Long>>?,
    val programAllocations: List<Allocation<Long>>?
)
