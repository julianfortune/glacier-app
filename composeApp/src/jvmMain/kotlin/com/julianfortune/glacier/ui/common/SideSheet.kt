package com.julianfortune.glacier.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun SideSheet(
    onClose: () -> Unit,
    isDismissable: Boolean = true,
    scrimAlpha: Float = 0.6f,
    color: Color = MaterialTheme.colorScheme.background,
    tonalElevation: Dp = 0.dp,
    content: @Composable (close: () -> Unit) -> Unit,
) {
    val isVisible = remember { MutableTransitionState(false).apply { targetState = true } }

    val close = {
        isVisible.targetState = false
    }

    val onDismissRequest = {
        if (isDismissable) {
            close()
        }
    }

    // Notify the parent when the side sheet has closed
    LaunchedEffect(isVisible.currentState, isVisible.targetState) {
        if (isVisible.targetState == false && isVisible.currentState == false) {
            onClose()
        }
    }

    val isTransitioning = isVisible.currentState != isVisible.targetState
    if (isTransitioning || isVisible.currentState) {
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true, // Allow 'esc' to close
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                // Darkening backdrop
                AnimatedVisibility(
                    visibleState = isVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = scrimAlpha))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onDismissRequest()
                            }
                    )
                }

                AnimatedVisibility(
                    visibleState = isVisible,
                    enter = slideIn(initialOffset = { IntOffset(x = it.width, y = 0) }),
                    exit = slideOut { IntOffset(x = it.width, y = 0) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight(),
                        tonalElevation = tonalElevation,
                        color = color,
                    ) {
                        content(close)
                    }
                }
            }
        }
    }
}
