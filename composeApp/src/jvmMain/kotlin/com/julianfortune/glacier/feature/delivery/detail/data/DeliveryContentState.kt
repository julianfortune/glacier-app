package com.julianfortune.glacier.feature.delivery.detail.data

import androidx.compose.ui.state.ToggleableState

data class DeliveryContentState(
    val receivedDate: String,
    val supplierName: String,
    val entrySelectionCount: Int,
    val entrySelectionState: ToggleableState,
    val entryRows: List<EntryRowState>,
    val totalCount: String,
    val totalWeight: String,
    val subtotal: String,
    val fees: String,
    val taxes: String,
    val totalCost: String,
)
