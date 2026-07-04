package com.julianfortune.glacier.feature.delivery.detail.data

import com.julianfortune.glacier.data.domain.Delivery

sealed interface EntryAction {
    data object CreateNew : EntryAction
    data class Edit(val entry: Delivery.Entry) : EntryAction
}

