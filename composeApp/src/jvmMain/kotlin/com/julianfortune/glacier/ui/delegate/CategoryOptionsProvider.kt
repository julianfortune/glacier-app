package com.julianfortune.glacier.ui.delegate

import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface CategoryOptionsProvider {
    val categoryOptions: Flow<List<Option<Long>>>
}
