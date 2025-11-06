package com.julianfortune.glacier.data.persisted

import com.julianfortune.glacier.data.WeightUnit

data class Item(
    override val id: Long,
    val name: String,
    val weightHundredths: Long,
    val weightUnits: WeightUnit,
    val categories: List<CategoryLink>
) : Persisted
