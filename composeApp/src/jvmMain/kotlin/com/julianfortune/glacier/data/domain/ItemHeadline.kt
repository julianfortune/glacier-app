package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.Entity

data class ItemHeadline(
    override val id: Long,
    val name: String,
) : Entity
