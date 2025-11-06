package com.julianfortune.glacier.data.persisted

data class CategoryLink(
    override val id: Long,
    val category: Category,
) : Persisted
