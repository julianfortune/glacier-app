package com.julianfortune.glacier.feature.delivery.page.data

import com.julianfortune.glacier.data.domain.Delivery

sealed interface DeliveryEntryAction {
    data object CreateNew : DeliveryEntryAction
    data class Edit(val index: Int, val entry: Delivery.Entry) : DeliveryEntryAction
}
