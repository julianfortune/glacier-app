package com.julianfortune.glacier.ui.common.data

/**
 * Used when the user needs to selection between multiple possibilities
 */
data class Option<ID>(
    val id: ID,
    val title: String,
)