package com.julianfortune.glacier.viewModel.data

import com.julianfortune.glacier.data.domain.entry.Entry

interface DeliveryEntryAction {
    data object CreateNew : DeliveryEntryAction
    data class Edit(val index: Int, val entry: Entry) : DeliveryEntryAction
}
