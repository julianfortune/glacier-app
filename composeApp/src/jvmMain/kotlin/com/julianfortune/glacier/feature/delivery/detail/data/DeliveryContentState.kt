package com.julianfortune.glacier.feature.delivery.detail.data

data class DeliveryContentState(
    val receivedDate: String,
    val supplierName: String,
    val entryRows: List<EntryRowState>,
    val totalCount: String,
    val totalWeight: String,
    val subtotal: String,
    val fees: String,
    val taxes: String,
    val totalCost: String,
)
