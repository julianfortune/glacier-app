package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.domain.item.WeightUnit

data class Item(
    val name: String,
    val weightHundredths: Long,
    val weightUnits: WeightUnit,
    val categoryIds: List<Long>
)
