package com.julianfortune.glacier.feature.delivery.detail.data

import com.julianfortune.glacier.feature.delivery.common.data.DeliveryBody

sealed interface DeliveryAction {
    data class Edit(val id: Long, val delivery: DeliveryBody) : DeliveryAction
    data class Delete(val id: Long) : DeliveryAction
}
