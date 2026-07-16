package com.julianfortune.glacier.ui.coordinator.delivery.data

import com.julianfortune.glacier.data.domain.Delivery

sealed interface DeliveryViewState {
    data object Empty : DeliveryViewState
    data object Loading : DeliveryViewState
    data class Viewing(
        val currentDelivery: Delivery,
    ) : DeliveryViewState
}