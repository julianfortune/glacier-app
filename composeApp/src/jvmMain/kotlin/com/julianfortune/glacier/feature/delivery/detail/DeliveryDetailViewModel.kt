package com.julianfortune.glacier.feature.delivery.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.core.util.formatCents
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.feature.delivery.common.data.DeliveryBody
import com.julianfortune.glacier.feature.delivery.common.data.EntryBody
import com.julianfortune.glacier.feature.delivery.detail.data.*
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.formatLocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.format.FormatStyle


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

    val selectedEntryRows = MutableStateFlow<Set<Long>>(emptySet())

    // Derived flow for selected item details
    private val delivery: StateFlow<Delivery?> = selectedDeliveryId
        .flatMapLatest { id ->
            id?.let { deliveryRepository.getDeliveryById(it) } ?: flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val uiState: StateFlow<DeliveryDetailState> = combine(delivery, selectedEntryRows) { delivery, selections ->
        delivery?.let {
            // TODO(P2): Clean up this mess
            val title = "Delivery " + formatLocalDate(
                delivery.received,
                FormatStyle.MEDIUM
            ) + " • ${delivery.supplier.name}"
            val totalCount = (delivery.entries.sumOf { it.unitCount }).toString()
            val totalWeight = calculateDeliveryTotalWeightPounds(delivery).toString()
            val subtotal = "$" + formatCents(calculateDeliverySubTotalCostCents(delivery))
            val fees = "$" + formatCents(delivery.feesCents ?: 0)
            val taxes = "$" + formatCents(delivery.taxesCents ?: 0)
            val total = "$" + formatCents(calculateDeliveryTotalCostCents(delivery))

            val entrySelectionState = when (selections.size) {
                0 -> ToggleableState.Off
                else -> {
                    val unselectedEntries = delivery.entries.map { it.id }.toSet().minus(selections)
                    when (unselectedEntries.size) {
                        0 -> ToggleableState.On
                        else -> ToggleableState.Indeterminate
                    }
                }
            }

            val content = DeliveryContentState(
                formatLocalDate(delivery.received, FormatStyle.MEDIUM),
                delivery.supplier.name,
                selections.size,
                entrySelectionState,
                delivery.entries.map { e ->
                    val totalWeight = calculateEntryTotalWeight(e)
                    val totalCostCents = "$" + formatCents(calculateEntryTotalCostCents(e))
                    EntryRowState(
                        e.id,
                        e.id in selections,
                        e.item.name,
                        null,
                        null,
                        e.unitCount.toString(),
                        totalWeight.toPounds().toString(),
                        totalCostCents,
                    )
                },
                totalCount,
                totalWeight,
                subtotal,
                fees,
                taxes,
                total,
            )

            DeliveryDetailState.Success(title, content)
        } ?: DeliveryDetailState.Failure
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeliveryDetailState.Loading
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

    val itemOptions = itemRepository.getAll()
        .map { items ->
            items.map { Option(it.id, it.name) }
        }
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

    fun showEditDelivery(deliveryId: Long) {
        // TODO(P2): Get rid of `!!`
        val body = delivery.value?.let {
            DeliveryBody(
                it.received,
                it.supplier.id,
                it.taxesCents,
                it.feesCents,
            )
        }!!

        _deliveryAction.value = DeliveryAction.Edit(deliveryId, body)
    }

    fun showDeleteDelivery(deliveryId: Long) {
        _deliveryAction.value = DeliveryAction.Delete(deliveryId)
    }

    fun cancelDeliveryOperation() {
        _deliveryAction.value = null
    }

    fun showEditEntry(entryId: Long) {
        // TODO(P1): Better error handling !
        val entry = delivery.value?.entries?.find { it.id == entryId }!!
        val body = EntryBody(
            entry.item.id,
            entry.unitCount,
            entry.unitWeight,
            entry.costStatus,
            entry.unitCostCents,
            entry.itemWeight,
            entry.itemsPerUnit,
            entry.program?.id,
            entry.purchasingAccount?.id,
        )

        _entryAction.value = EntryAction.Edit(entry.id, body)
    }

    fun showDeleteEntry(entryId: Long) {
        _entryAction.value = EntryAction.Delete(entryId)
    }

    fun cancelEntryOperation() {
        _entryAction.value = null
    }

    fun onToggleALlEntriesSelection() {
        when (selectedEntryRows.value.size) {
            0 -> selectedEntryRows.value = delivery.value?.entries?.map { it.id }?.toSet() ?: emptySet()
            else -> selectedEntryRows.value = emptySet()
        }
    }

    fun onToggleEntrySelection(isSelected: Boolean, entryId: Long) {
        when {
            isSelected -> selectedEntryRows.value = selectedEntryRows.value.plus(entryId)
            else -> selectedEntryRows.value = selectedEntryRows.value.minus(entryId)
        }
    }

    fun clearEntrySelection() {
        selectedEntryRows.value = emptySet()
    }
}


fun calculateEntryTotalCostCents(entry: Delivery.Entry): Long {
    if (entry.costStatus == CostStatus.NO_COST) {
        return 0L
    }

    return entry.unitCount * entry.unitCostCents
}

fun calculateDeliverySubTotalCostCents(delivery: Delivery): Long {
    val totalUnitsCost = delivery.entries.map { calculateEntryTotalCostCents(it) }.reduceOrNull { a, b -> a + b } ?: 0

    return totalUnitsCost
}

fun calculateDeliveryTotalCostCents(delivery: Delivery): Long {
    val totalUnitsCost = delivery.entries.map { calculateEntryTotalCostCents(it) }.reduceOrNull { a, b -> a + b } ?: 0

    return totalUnitsCost + (delivery.feesCents ?: 0) + (delivery.taxesCents ?: 0)
}

fun calculateEntryTotalWeight(entry: Delivery.Entry): Weight {
    return entry.unitWeight.times(entry.unitCount)
}

fun calculateDeliveryTotalWeightPounds(delivery: Delivery): Double {
    return (delivery.entries).fold(0.0) { sum, entry ->
        sum + calculateEntryTotalWeight(entry).toPounds()
    }
}
