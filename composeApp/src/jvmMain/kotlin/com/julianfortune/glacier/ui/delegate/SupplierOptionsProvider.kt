package com.julianfortune.glacier.ui.delegate

import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface SupplierOptionsProvider {
    val supplierOptions: Flow<List<Option<Long>>>
}