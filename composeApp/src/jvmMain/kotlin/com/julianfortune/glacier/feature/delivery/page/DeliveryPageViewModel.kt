package com.julianfortune.glacier.feature.delivery.page

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryEntryAction
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DeliveryBody(
    val received: LocalDate,
    // ...
)

data class EntryBody(
    val itemId: Long,
    val unitCount: Long,
    val unitWeight: Weight,
    val costStatus: CostStatus,
    val unitCostCents: Long,
    val itemWeight: Weight?,
    val itemsPerUnit: Long?,
    val programId: Long?,
    val purchasingAccountId: Long?,
)

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

    val allItems: StateFlow<List<ItemHeadline>> =
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
            deliveryRepository.deleteDeliveryById(deliveryId)
        }
    }

    fun saveEntry(
        deliveryId: Long,
        body: EntryBody,
    ) {
        viewModelScope.launch {
            deliveryRepository.appendDeliveryEntry(
                deliveryId,
                body.itemId,
                body.unitCount,
                body.unitWeight,
                body.costStatus,
                body.unitCostCents,
                body.itemWeight,
                body.itemsPerUnit,
                body.programId,
                body.purchasingAccountId,
            )
        }
    }

    fun updateEntry(
        entryId: Long,
        body: EntryBody,
    ) {
        viewModelScope.launch {
            deliveryRepository.updateDeliveryEntry(
                entryId,
                body.itemId,
                body.unitCount,
                body.unitWeight,
                body.costStatus,
                body.unitCostCents,
                body.itemWeight,
                body.itemsPerUnit,
                body.programId,
                body.purchasingAccountId,
            )
        }
    }

    fun deleteEntryById(id: Long) {
        viewModelScope.launch {
            deliveryRepository.deleteDeliveryEntryById(id)
        }
    }

    fun showNewEntry() {
        _deliveryEntryAction.value = DeliveryEntryAction.CreateNew
    }

    fun showEditEntry(entry: Delivery.Entry) {
        _deliveryEntryAction.value = DeliveryEntryAction.Edit(entry)
    }

    fun cancelEntryOperation() {
        _deliveryEntryAction.value = null
    }

}