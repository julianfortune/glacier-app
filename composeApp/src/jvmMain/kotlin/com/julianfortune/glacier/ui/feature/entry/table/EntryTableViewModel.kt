package com.julianfortune.glacier.ui.feature.entry.table

import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.core.util.formatCents
import com.julianfortune.glacier.core.viewer.DeliveryViewer
import com.julianfortune.glacier.core.viewer.data.DeliveryViewerState
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.provider.ItemOptionsProvider
import com.julianfortune.glacier.ui.feature.delivery.detail.calculateDeliverySubTotalCostCents
import com.julianfortune.glacier.ui.feature.delivery.detail.calculateDeliveryTotalWeightPounds
import com.julianfortune.glacier.ui.feature.delivery.detail.calculateEntryTotalCostCents
import com.julianfortune.glacier.ui.feature.delivery.detail.calculateEntryTotalWeight
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryBody
import com.julianfortune.glacier.ui.feature.entry.table.data.EntryAction
import com.julianfortune.glacier.ui.feature.entry.table.data.EntryRowState
import com.julianfortune.glacier.ui.feature.entry.table.data.EntryTableState
import com.julianfortune.glacier.ui.feature.entry.table.data.SelectionState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EntryTableViewModel(
    private val deliveryViewer: DeliveryViewer,
    private val deliveryRepository: DeliveryRepository,
    itemOptionsProvider: ItemOptionsProvider
) : ViewModel(), ItemOptionsProvider by itemOptionsProvider {

    private val entryAction = MutableStateFlow<EntryAction?>(null)
    private val selectionEnabled = MutableStateFlow(false)
    private val selectedEntryRows = MutableStateFlow<Set<Long>>(emptySet())

    val uiState: StateFlow<EntryTableState?> = combine(
        deliveryViewer.state,
        entryAction,
        selectionEnabled,
        selectedEntryRows,
    ) { viewerState, entryAction, selectionEnabled, selections ->
        when (viewerState) {
            is DeliveryViewerState.Empty, is DeliveryViewerState.Loading -> null
            is DeliveryViewerState.Viewing -> {
                val delivery = viewerState.currentDelivery

                val selectionState = when {
                    selectionEnabled -> {
                        val selectAll = when (selections.size) {
                            0 -> ToggleableState.Off
                            else -> {
                                val unselectedEntries = delivery.entries.map { it.id }.toSet().minus(selections)
                                when (unselectedEntries.size) {
                                    0 -> ToggleableState.On
                                    else -> ToggleableState.Indeterminate
                                }
                            }
                        }
                        SelectionState.Enabled(selections.size, selectAll)
                    }

                    else -> SelectionState.Disabled
                }

                val entryRows = delivery.entries.map { e ->
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
                }

                val totalCount = (delivery.entries.sumOf { it.unitCount }).toString()
                val totalWeight = calculateDeliveryTotalWeightPounds(delivery).toString()
                val subtotal = "$" + formatCents(calculateDeliverySubTotalCostCents(delivery))

                EntryTableState(
                    entryRows,
                    selectionState,
                    entryAction,
                    totalCount,
                    totalWeight,
                    subtotal
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun showAddEntry() {
        entryAction.value = EntryAction.Add
    }

    fun showEditEntry(entryId: Long) {
        when (val current = deliveryViewer.state.value) {
            is DeliveryViewerState.Viewing -> {
                // TODO(P1): Better error handling ...
                val entry = current.currentDelivery.entries.find { it.id == entryId }!!
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

                entryAction.value = EntryAction.Edit(entry.id, body)
            }

            else -> {
                // TODO(P2): Error handling ...
            }
        }
    }

    fun showDeleteEntry(entryId: Long) {
        entryAction.value = EntryAction.Delete(entryId)
    }

    fun cancelEntryOperation() {
        entryAction.value = null
    }

    fun enableEntrySelection() {
        selectionEnabled.value = true
    }

    fun disableEntrySelection() {
        selectionEnabled.value = false
    }

    fun onToggleAllEntriesSelection() {
        val current = deliveryViewer.state.value

        if (current !is DeliveryViewerState.Viewing) {
            // TODO(P2): Error handling ...
            return
        }

        when (selectedEntryRows.value.size) {
            0 -> selectedEntryRows.value = current.currentDelivery.entries.map { it.id }.toSet()
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

    fun saveEntry(body: EntryBody) {
        val current = deliveryViewer.state.value

        if (current !is DeliveryViewerState.Viewing) {
            // TODO(P2): Error handling ...
            return
        }

        viewModelScope.launch {
            deliveryRepository.appendDeliveryEntry(
                current.currentDelivery.id,
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

}