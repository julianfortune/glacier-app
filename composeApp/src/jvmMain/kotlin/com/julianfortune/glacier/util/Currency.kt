package com.julianfortune.glacier.util

// Without any '$'
fun formatCents(cents: Long) : String {
    val dollars = cents / 100
    val remainingCents = cents % 100
    return if (remainingCents == 0L) {
        "$dollars"
    } else {
        String.format("%d.%02d", dollars, remainingCents)
    }
}
