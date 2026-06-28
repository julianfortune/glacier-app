package com.julianfortune.glacier.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.repository.SupplierRepository
import com.julianfortune.glacier.viewModel.data.DeliveryEntryAction
import com.julianfortune.glacier.viewModel.data.EntityOperation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryListViewModel(
    private val deliveryRepository: DeliveryRepository,
    itemRepository: ItemRepository,
    supplierRepository: SupplierRepository
) : ViewModel() {
    // TODO(P3): Sorting, default: By receivedDate and then createdDatetime
    // TODO(P5): Filtering, e.g., by time period
    val allDeliveries: StateFlow<List<Entity<DeliveryHeadline>>> =
        deliveryRepository
            .getAllAsHeadlines()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    val allSuppliers: StateFlow<List<Entity<Supplier>>> =
        supplierRepository.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    val supplierMap: StateFlow<Map<Long, Entity<Supplier>>> =
        supplierRepository.getAll()
            .map { suppliers ->
                suppliers.associateBy { it.id }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyMap()
            )

    val allItems: StateFlow<List<Entity<Item>>> =
        itemRepository.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun saveDelivery(delivery: DeliveryDetail) {
        viewModelScope.launch {
            val newDeliveryId = deliveryRepository.insert(delivery)
        }
    }
}
