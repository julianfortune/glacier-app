package com.julianfortune.glacier.ui.feature.item.data

import com.julianfortune.glacier.data.domain.Item

data class ItemBody(
    val name: String,
    val categoryId: Long?,
    val format: Item.Format,
)
