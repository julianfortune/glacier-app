package com.julianfortune.glacier.ui.common.provider

import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultSupplierOptionsProvider(
    supplierRepository: SupplierRepository,
    scope: CoroutineScope
) : SupplierOptionsProvider {

    override val supplierOptions = supplierRepository.getAll()
        .map { suppliers ->
            suppliers.map { Option(it.id, it.name) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )
    
}