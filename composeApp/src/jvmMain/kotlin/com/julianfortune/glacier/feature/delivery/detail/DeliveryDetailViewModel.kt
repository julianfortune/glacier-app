package com.julianfortune.glacier.feature.delivery.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.*
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.feature.delivery.common.data.DeliveryBody
import com.julianfortune.glacier.feature.delivery.detail.data.DeliveryAction
import com.julianfortune.glacier.feature.delivery.detail.data.EntryAction
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
class DeliveryDetailViewModel(
    private val deliveryRepository: DeliveryRepository,
    itemRepository: ItemRepository,
    supplierRepository: SupplierRepository,
) : ViewModel() {

    private val selectedDeliveryId = MutableStateFlow<Long?>(null)

    private val _deliveryAction = mutableStateOf<DeliveryAction?>(null)
    val deliveryAction: State<DeliveryAction?> = _deliveryAction

    private val _entryAction = mutableStateOf<EntryAction?>(null)
    val entryAction: State<EntryAction?> = _entryAction

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

    val supplierOptions = supplierRepository.getAll()
        .map { suppliers ->
            suppliers.map { Option(it.id, it.name) }
        }
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

    fun updateDelivery(id: Long, delivery: DeliveryBody) {
        viewModelScope.launch {
            deliveryRepository.updateDelivery(
                id,
                delivery.received,
                delivery.supplierId,
                delivery.taxesCents,
                delivery.feesCents,
            )
        }
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
        _entryAction.value = EntryAction.CreateNew
    }

    fun showEditEntry(entry: Delivery.Entry) {
        _entryAction.value = EntryAction.Edit(entry)
    }

    fun cancelEntryOperation() {
        _entryAction.value = null
    }

}