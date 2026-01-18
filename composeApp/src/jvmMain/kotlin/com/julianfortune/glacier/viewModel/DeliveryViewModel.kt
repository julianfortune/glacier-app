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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val itemRepository: ItemRepository,
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val _newDeliveryDialogIsVisible = mutableStateOf(false)
    val newDeliveryDialogIsVisible: State<Boolean> = _newDeliveryDialogIsVisible

    private val _newEntryDialogIsVisible = mutableStateOf(false)
    val newEntryDialogIsVisible: State<Boolean> = _newEntryDialogIsVisible

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


    private val _selectedDeliveryId = MutableStateFlow<Long?>(null)
    val selectedDeliveryId: StateFlow<Long?> = _selectedDeliveryId

    // Derived flow for selected item details
    val selectedDeliveryDetail: StateFlow<Entity<DeliveryDetail>?> = _selectedDeliveryId
        .flatMapLatest { id ->
            id?.let { deliveryRepository.getDeliveryDetailById(it) } ?: flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    suspend fun getItemById(itemId: Long): Entity<Item> {
        return itemRepository.getById(itemId)
    }

//    suspend fun getSupplierById(supplierId: Long): Entity<Supplier> {
//        return supplierRepository.getById(supplierId)
//    }

    fun selectDelivery(deliveryId: Long) {
        _selectedDeliveryId.value = deliveryId
    }

    suspend fun saveDelivery(delivery: DeliveryDetail): Long {
        return deliveryRepository.insert(delivery)
    }

    suspend fun updateDelivery(delivery: Entity<DeliveryDetail>) {
        TODO()
    }

    suspend fun saveEntry(deliveryId: Long, entry: Entry) {
        deliveryRepository.insertDeliveryEntry(deliveryId, entry)
    }

    suspend fun updateEntry(deliveryId: Long, entry: Entity<Entry>) {
        // TODO(P1): Editing entries
    }

    suspend fun deleteEntry(deliveryId: Long, entryId: Long) {
        // TODO(P1): Deleting entries
    }

    fun showNewDelivery() {
        _newDeliveryDialogIsVisible.value = true
    }

    fun dismissNewDelivery() {
        _newDeliveryDialogIsVisible.value = false
    }

    fun newDeliveryCreated(id: Long) {
        _newDeliveryDialogIsVisible.value = false
        _selectedDeliveryId.value = id
    }

    fun showNewEntry() {
        _newEntryDialogIsVisible.value = true
    }

    fun dismissNewEntry() {
        _newEntryDialogIsVisible.value = false
    }

}
