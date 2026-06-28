package com.julianfortune.glacier.feature.delivery.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.ui.layout.ListDetailControllable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryPageViewModel(
    private val deliveryRepository: DeliveryRepository,
    itemRepository: ItemRepository,
    supplierRepository: SupplierRepository,
) : ViewModel(), ListDetailControllable {
    private val _selectedDeliveryId = MutableStateFlow<Long?>(null)
    val selectedDeliveryId: StateFlow<Long?> = _selectedDeliveryId

    // Derived flow for selected item details
    val deliveryDetail: StateFlow<Entity<DeliveryDetail>?> = _selectedDeliveryId
        .flatMapLatest { id ->
            id?.let { deliveryRepository.getDeliveryDetailById(it) } ?: flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
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

    val itemMap: StateFlow<Map<Long, Entity<Item>>> =
        itemRepository.getAll()
            .map { items ->
                items.associateBy { it.id }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyMap()
            )

    override fun setCurrentId(id: Long?) {
        _selectedDeliveryId.value = id
    }

    fun updateDelivery(delivery: Entity<DeliveryDetail>) {
        viewModelScope.launch {
            deliveryRepository.update(delivery)
        }
    }

    fun deleteDelivery(deliveryId: Long) {
        viewModelScope.launch {
            deliveryRepository.deleteById(deliveryId)
        }
    }

    fun saveEntry(deliveryId: Long, entry: Entry) {
        viewModelScope.launch {
            deliveryRepository.insertDeliveryEntry(deliveryId, entry)
        }
    }

}