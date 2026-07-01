package com.julianfortune.glacier.feature.delivery.page

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.repository.SupplierRepository
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryEntryAction
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
    private val selectedDeliveryId = MutableStateFlow<Long?>(null)

    private val _deliveryEntryAction = mutableStateOf<DeliveryEntryAction?>(null)
    val deliveryEntryAction: State<DeliveryEntryAction?> = _deliveryEntryAction

    // Derived flow for selected item details
    val deliveryDetail: StateFlow<Entity<DeliveryDetail>?> = selectedDeliveryId
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
        selectedDeliveryId.value = id
    }

    fun deleteDelivery(deliveryId: Long) {
        selectedDeliveryId.value = null
        viewModelScope.launch {
            deliveryRepository.deleteById(deliveryId)
        }
    }

    fun saveEntry(deliveryId: Long, entry: Entry) {
        viewModelScope.launch {
            deliveryRepository.insertDeliveryEntry(deliveryId, entry)
        }
    }

    fun deleteEntry(entryId: Long) {
        
    }

    fun showNewEntry() {
        _deliveryEntryAction.value = DeliveryEntryAction.CreateNew
    }

    fun showEditEntry(index: Int, entry: Entry) {
        _deliveryEntryAction.value = DeliveryEntryAction.Edit(index, entry)
    }

    fun cancelEntryOperation() {
        _deliveryEntryAction.value = null
    }

}