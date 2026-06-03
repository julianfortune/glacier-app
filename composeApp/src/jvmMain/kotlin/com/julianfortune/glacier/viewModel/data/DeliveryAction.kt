package com.julianfortune.glacier.viewModel.data

import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail

sealed interface DeliveryAction {
    data object CreateNew : DeliveryAction
    data class Edit(val delivery: Entity<DeliveryDetail>) : DeliveryAction
    data class Delete(val deliveryId: Long) : DeliveryAction
}