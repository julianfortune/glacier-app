package com.julianfortune.glacier.feature.delivery.page.data

import com.julianfortune.glacier.data.domain.entry.Entry

sealed interface DeliveryEntryAction {
    data object CreateNew : DeliveryEntryAction
    data class Edit(val index: Int, val entry: Entry) : DeliveryEntryAction
}
