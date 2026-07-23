package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.Entity

data class Item(
    override val id: Long,
    val name: String,
    val categories: List<Category>,
    val savedWeights: Set<Weight>?
) : Entity
