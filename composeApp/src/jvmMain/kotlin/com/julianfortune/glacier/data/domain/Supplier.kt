package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.NamedEntity

data class Supplier(
    override val name: String,
) : NamedEntity
