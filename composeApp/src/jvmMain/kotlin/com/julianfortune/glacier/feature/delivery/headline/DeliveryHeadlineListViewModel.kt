package com.julianfortune.glacier.feature.delivery.headline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.DeliveryHeadline
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.repository.SupplierRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryHeadlineListViewModel(
    private val deliveryRepository: DeliveryRepository,
    itemRepository: ItemRepository,
    supplierRepository: SupplierRepository
) : ViewModel() {
    // TODO(P3): Sorting, default: By receivedDate and then createdDatetime
    // TODO(P5): Filtering, e.g., by time period
    val allDeliveries: StateFlow<List<DeliveryHeadline>> =
        deliveryRepository
            .getAllAsHeadlines()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
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

    fun saveDelivery(delivery: Delivery) {
        viewModelScope.launch {
            TODO("Not implemented")
        }
    }
}