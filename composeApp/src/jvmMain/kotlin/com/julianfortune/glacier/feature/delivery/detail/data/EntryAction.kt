package com.julianfortune.glacier.feature.delivery.detail.data

import com.julianfortune.glacier.feature.delivery.common.data.EntryBody

sealed interface EntryAction {
    data object CreateNew : EntryAction
    data class Edit(val id: Long, val entry: EntryBody) : EntryAction
    data class Delete(val id: Long) : EntryAction
}
