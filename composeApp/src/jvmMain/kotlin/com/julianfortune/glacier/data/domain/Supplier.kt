package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.NamedEntity

data class Supplier(
    override val id: Long,
    override val name: String,
) : NamedEntity
