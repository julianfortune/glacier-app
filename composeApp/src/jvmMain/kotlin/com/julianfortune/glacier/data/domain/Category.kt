package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.NamedEntity

data class Category(
    override val name: String,
) : NamedEntity
