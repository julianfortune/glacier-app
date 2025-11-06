package com.julianfortune.glacier.data.persisted

data class Program(
    override val id: Long,
    val name: String,
) : Persisted
