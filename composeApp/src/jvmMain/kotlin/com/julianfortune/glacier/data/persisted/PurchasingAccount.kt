package com.julianfortune.glacier.data.persisted

data class PurchasingAccount(
    override val id: Long,
    val name: String,
) : Persisted
