package com.julianfortune.glacier.feature.delivery.page

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryEntryAction
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.repository.SupplierRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryPageViewModel(
    private val deliveryRepository: DeliveryRepository,
    itemRepository: ItemRepository,
    supplierRepository: SupplierRepository,
) : ViewModel() {
    private val selectedDeliveryId = MutableStateFlow<Long?>(null)

    private val _deliveryEntryAction = mutableStateOf<DeliveryEntryAction?>(null)
    val deliveryEntryAction: State<DeliveryEntryAction?> = _deliveryEntryAction

    // Derived flow for selected item details
    val delivery: StateFlow<Delivery?> = selectedDeliveryId
        .flatMapLatest { id ->
            id?.let { deliveryRepository.getDeliveryById(it) } ?: flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allSuppliers: StateFlow<List<Supplier>> =
        supplierRepository.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    val allItems: StateFlow<List<Item>> =
        itemRepository.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun setCurrentId(id: Long?) {
        selectedDeliveryId.value = id
    }

    fun deleteDelivery(deliveryId: Long) {
        selectedDeliveryId.value = null
        viewModelScope.launch {
            deliveryRepository.deleteById(deliveryId)
        }
    }

    fun saveEntry(deliveryId: Long, entry: Delivery.Entry) {
        viewModelScope.launch {
            TODO("Not implemented yet")
//            deliveryRepository.insertDeliveryEntry(deliveryId, entry)
        }
    }

    fun deleteEntryByIndex(delivery: Delivery, index: Int) {
        TODO()
    }

    fun showNewEntry() {
        _deliveryEntryAction.value = DeliveryEntryAction.CreateNew
    }

    fun showEditEntry(index: Int, entry: Delivery.Entry) {
        _deliveryEntryAction.value = DeliveryEntryAction.Edit(index, entry)
    }

    fun cancelEntryOperation() {
        _deliveryEntryAction.value = null
    }

}