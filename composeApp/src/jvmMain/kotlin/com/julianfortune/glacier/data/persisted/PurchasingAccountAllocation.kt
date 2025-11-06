package com.julianfortune.glacier.data.persisted

import com.julianfortune.glacier.data.Percentage

data class PurchasingAccountAllocation(
    override val id: Long,
    val percentage: Percentage,
    val purchasingAccount: PurchasingAccount,
) : Persisted
