package com.julianfortune.glacier.ui.theme

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.ui.graphics.Color

fun darkScrollbarStyle(): ScrollbarStyle {
    return defaultScrollbarStyle().copy(
        unhoverColor = Color.White.copy(alpha = 0.25f),
        hoverColor = Color.White.copy(alpha = 0.65f)
    )
}