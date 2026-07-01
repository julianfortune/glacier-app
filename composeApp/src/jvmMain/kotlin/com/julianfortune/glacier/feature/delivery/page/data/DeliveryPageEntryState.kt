package com.julianfortune.glacier.feature.delivery.page.data

data class EntryRowState(
    val itemName: String,
    val programName: String?,
    val accountName: String?,
    val unitCount: String,
    val totalWeight: String,
    val totalCost: String,
)

data class DeliveryPageEntryState(
    val entryRows: List<EntryRowState>,
    val totalCount: String,
    val totalWeight: String,
    val totalCost: String,
)
