package com.julianfortune.glacier.data.model

import com.julianfortune.glacier.data.common.Entity

data class ItemHeadline(
    override val id: Long,
    val name: String,
) : Entity
