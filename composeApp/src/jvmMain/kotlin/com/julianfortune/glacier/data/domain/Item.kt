package com.julianfortune.glacier.data.domain

data class Item(
    val name: String,
    val description: String?,
    val weight: Weight?,
    val categoryIds: List<Long>
)
