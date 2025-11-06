package com.julianfortune.glacier.data

data class DeliveryEntry(
    val itemId: Long,
    val itemCount: Long,
    val costStatus: CostStatus,
    val itemCostCents: Long,
    val aggregate: DeliveryEntryAggregation?,
)
