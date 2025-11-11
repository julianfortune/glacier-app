package com.julianfortune.glacier.data.domain.entry

data class Entry(
    val itemId: Long,
    val itemCount: Long,
    val costStatus: CostStatus,
    val itemCostCents: Long,
    val aggregate: EntryAggregation?,
    val purchasingAccountAllocations: List<Allocation<Long>>?,
    val programAllocations: List<Allocation<Long>>?
)
