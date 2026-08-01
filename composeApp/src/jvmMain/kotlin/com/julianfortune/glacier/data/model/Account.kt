package com.julianfortune.glacier.data.model

import com.julianfortune.glacier.data.common.NamedEntity

data class Account(
    override val id: Long,
    override val name: String,
) : NamedEntity
