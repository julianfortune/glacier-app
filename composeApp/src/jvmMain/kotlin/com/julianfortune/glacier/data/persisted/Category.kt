package com.julianfortune.glacier.data.persisted

data class Category(
    override val id: Long,
    val name: String
) : Persisted
