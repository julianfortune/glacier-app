package com.julianfortune.glacier.data.common

import com.julianfortune.glacier.ui.common.data.Option

interface NamedEntity : Entity {
    val name: String

    fun toOption(): Option<Long> = Option(id, name)
}