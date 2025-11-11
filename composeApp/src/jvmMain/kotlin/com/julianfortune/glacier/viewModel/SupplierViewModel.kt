package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.repository.SupplierRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SupplierViewModel(private val supplierRepository: SupplierRepository) : ViewModel() {
    // TODO: Understand what this is doing
    // TODO: Does this refresh automatically ? Seems like it should
    val suppliers = supplierRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun deleteSupplier(id: Long) {
        viewModelScope.launch {
            supplierRepository.deleteById(id)
        }
    }
}
