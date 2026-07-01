package com.julianfortune.glacier.data.domain.entry

import com.julianfortune.glacier.data.domain.Weight

data class Entry(
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
