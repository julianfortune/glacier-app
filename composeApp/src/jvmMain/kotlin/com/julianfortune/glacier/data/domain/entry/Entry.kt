package com.julianfortune.glacier.data.domain.entry

data class Entry(
    val itemId: Long,
    val itemCount: Long,
    val costStatus: CostStatus,
    val itemCostCents: Long,
    val aggregate: EntryAggregation?,
    val purchasingAccountIds: List<Allocation<Long>>?,
    val programIds: List<Allocation<Long>>?
)