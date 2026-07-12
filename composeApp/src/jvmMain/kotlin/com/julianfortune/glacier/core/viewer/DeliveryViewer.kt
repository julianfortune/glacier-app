package com.julianfortune.glacier.core.viewer

import com.julianfortune.glacier.core.viewer.data.DeliveryViewerState
import kotlinx.coroutines.flow.StateFlow

interface DeliveryViewer {
    val state: StateFlow<DeliveryViewerState>

    fun view(deliveryId: Long)

    fun clear()
}