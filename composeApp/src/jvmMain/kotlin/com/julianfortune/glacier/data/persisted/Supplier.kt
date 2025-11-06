package com.julianfortune.glacier.data.persisted

data class Supplier(
    override val id: Long,
    val name: String,
) : Persisted
