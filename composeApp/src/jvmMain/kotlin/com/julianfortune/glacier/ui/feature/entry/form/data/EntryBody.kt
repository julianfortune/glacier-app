package com.julianfortune.glacier.ui.feature.entry.form.data

import com.julianfortune.glacier.data.model.CostStatus
import com.julianfortune.glacier.data.model.Weight

data class EntryBody(
    val itemId: Long,
    val unitCount: Long,
    val unitWeight: Weight,
    val costStatus: CostStatus,
    val unitCostCents: Long,
    val itemWeight: Weight?,
    val itemsPerUnit: Long?,
    val programId: Long?,
    val accountId: Long?,
)
