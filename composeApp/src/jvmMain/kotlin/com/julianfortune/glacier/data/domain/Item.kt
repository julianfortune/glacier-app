package com.julianfortune.glacier.data.domain

data class Item(
    val name: String,
    val categoryIds: List<Long>,
    // TODO: Saved weights
)
