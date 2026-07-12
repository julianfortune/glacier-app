package com.julianfortune.glacier.core.viewer.data

import com.julianfortune.glacier.data.domain.Delivery

sealed interface DeliveryViewerState {
    data object Empty : DeliveryViewerState
    data object Loading : DeliveryViewerState
    data class Viewing(
        val currentDelivery: Delivery,
    ) : DeliveryViewerState
}
