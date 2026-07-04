package com.julianfortune.glacier.feature.delivery.detail.data

data class EntryRowState(
    val entryId: Long,
    val itemName: String,
    val programName: String?,
    val accountName: String?,
    val unitCount: String,
    val totalWeight: String,
    val totalCost: String,
)