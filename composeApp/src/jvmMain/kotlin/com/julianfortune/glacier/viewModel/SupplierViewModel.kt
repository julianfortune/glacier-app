package com.julianfortune.glacier.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.repository.SupplierRepository
import com.julianfortune.glacier.viewModel.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SupplierViewModel(private val supplierRepository: SupplierRepository) : ViewModel() {

    private val _supplierOperation = mutableStateOf<EntityOperation<Supplier>?>(null)
    val supplierOperation: State<EntityOperation<Supplier>?> = _supplierOperation

    // TODO: Understand what this is doing
    // TODO: Does this refresh automatically ? Seems like it should
    val suppliers = supplierRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun saveSupplier(supplier: Supplier) {
        supplierRepository.insert(supplier)
    }

    suspend fun updateSupplier(supplier: Entity<Supplier>) {
        supplierRepository.update(supplier)
    }

    suspend fun deleteSupplier(supplierId: Long) {
        supplierRepository.deleteById(supplierId)
    }

    fun showNewSupplier() {
        _supplierOperation.value = EntityOperation.CreateNew
    }

    fun showEditSupplier(supplier: Entity<Supplier>) {
        _supplierOperation.value = EntityOperation.Edit(supplier)
    }

    fun showDeleteSupplier(supplier: Entity<Supplier>) {
        _supplierOperation.value = EntityOperation.Delete(supplier.id)
    }

    fun dismissSupplierModal() {
        _supplierOperation.value = null
    }

    fun cancelSupplierOperation() {
        _supplierOperation.value = null
    }
}
