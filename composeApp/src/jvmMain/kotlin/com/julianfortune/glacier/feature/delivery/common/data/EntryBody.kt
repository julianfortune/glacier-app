package com.julianfortune.glacier.feature.delivery.common.data

import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.Weight

data class EntryBody(
    val itemId: Long,
    val unitCount: Long,
    val unitWeight: Weight,
    val costStatus: CostStatus,
    val unitCostCents: Long,
    val itemWeight: Weight?,
    val itemsPerUnit: Long?,
    val programId: Long?,
    val purchasingAccountId: Long?,
)
