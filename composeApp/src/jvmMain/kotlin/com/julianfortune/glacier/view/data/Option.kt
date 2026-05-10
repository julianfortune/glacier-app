package com.julianfortune.glacier.view.data

/**
 * Used when the user needs to selection between multiple possibilities
 */
data class Option<ID>(
    val id: ID,
    val title: String,
)