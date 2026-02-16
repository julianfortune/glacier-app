package com.julianfortune.glacier.data.domain

data class Item(
    val name: String,
    val description: String?,
    val weightGrams: Long?,
    val categoryIds: List<Long>
)
