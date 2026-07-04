package com.julianfortune.glacier.feature.delivery.detail.data

data class EntryRowState(
    val itemName: String,
    val programName: String?,
    val accountName: String?,
    val unitCount: String,
    val totalWeight: String,
    val totalCost: String,
)