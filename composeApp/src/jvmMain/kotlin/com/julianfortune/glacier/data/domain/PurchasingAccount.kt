package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.NamedEntity

data class PurchasingAccount(
    override val name: String,
) : NamedEntity
