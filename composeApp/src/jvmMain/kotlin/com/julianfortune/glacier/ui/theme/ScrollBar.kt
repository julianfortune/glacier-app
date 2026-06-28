package com.julianfortune.glacier.ui.theme

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.ui.graphics.Color

fun dynamicScrollbarStyle(onSurfaceColor: Color): ScrollbarStyle {
    return defaultScrollbarStyle().copy(
        unhoverColor = onSurfaceColor.copy(alpha = 0.2f),
        hoverColor = onSurfaceColor.copy(alpha = 0.6f),
    )
}
