package com.julianfortune.glacier.ui.common.provider

import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface ItemOptionsProvider {
    val itemOptions: Flow<List<Option<Long>>>
}
