package com.julianfortune.glacier.ui.common.provider

import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface PurchasingAccountOptionsProvider {
    val accountOptions: Flow<List<Option<Long>>>
}
