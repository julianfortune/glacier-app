package com.julianfortune.glacier.ui.delegate

import com.julianfortune.glacier.data.repository.SupplierRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultSupplierOptionsProvider(
    supplierRepository: SupplierRepository,
    scope: CoroutineScope
) : SupplierOptionsProvider {

    override val supplierOptions = supplierRepository.getAll()
        .map { entities -> entities.map { it.toOption() } }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )
    
}