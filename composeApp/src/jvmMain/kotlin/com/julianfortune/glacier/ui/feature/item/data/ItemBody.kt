package com.julianfortune.glacier.ui.page.item.data

import com.julianfortune.glacier.data.domain.Weight

data class ItemBody(
    val name: String,
    val categoryId: Long?,
    val savedWeights: Set<Weight>?
)
