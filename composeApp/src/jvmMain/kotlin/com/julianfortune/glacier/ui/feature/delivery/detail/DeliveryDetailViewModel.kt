package com.julianfortune.glacier.ui.feature.delivery.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.core.util.formatCents
import com.julianfortune.glacier.core.viewer.DeliveryViewer
import com.julianfortune.glacier.core.viewer.data.DeliveryViewerState
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.ui.feature.delivery.form.data.DeliveryBody
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryBody
import com.julianfortune.glacier.ui.feature.delivery.detail.data.DeliveryAction
import com.julianfortune.glacier.ui.feature.delivery.detail.data.DeliveryContentState
import com.julianfortune.glacier.ui.feature.delivery.detail.data.DeliveryDetailState
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.formatLocalDate
import com.julianfortune.glacier.ui.common.provider.SupplierOptionsProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.FormatStyle


@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryDetailViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val deliveryViewer: DeliveryViewer,
    supplierOptionsProvider: SupplierOptionsProvider
) : ViewModel(), SupplierOptionsProvider by supplierOptionsProvider {

    private val _deliveryAction = mutableStateOf<DeliveryAction?>(null)
    val deliveryAction: State<DeliveryAction?> = _deliveryAction

    val uiState: StateFlow<DeliveryDetailState> = deliveryViewer.state.map { viewerState ->
        when (viewerState) {
            is DeliveryViewerState.Empty -> DeliveryDetailState.Loading
            is DeliveryViewerState.Loading -> DeliveryDetailState.Loading
            is DeliveryViewerState.Viewing -> {
                val delivery = viewerState.currentDelivery
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

                val content = DeliveryContentState(
                    delivery.id,
                    formatLocalDate(delivery.received, FormatStyle.MEDIUM),
                    delivery.supplier.name,
                    totalCount,
                    totalWeight,
                    subtotal,
                    fees,
                    taxes,
                    total,
                )

                DeliveryDetailState.Success(
                    title = title,
                    content = content,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeliveryDetailState.Loading
    )

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
        viewModelScope.launch {
            deliveryRepository.deleteDeliveryById(deliveryId)
            deliveryViewer.clear()
        }
    }

    fun showEditDelivery() {
        when (val current = deliveryViewer.state.value) {
            is DeliveryViewerState.Viewing -> {
                val delivery = current.currentDelivery
                val body = DeliveryBody(
                    delivery.received,
                    delivery.supplier.id,
                    delivery.taxesCents,
                    delivery.feesCents,
                )
                _deliveryAction.value = DeliveryAction.Edit(delivery.id, body)
            }

            else -> {
                // TODO(P2): Error handling ...
            }
        }
    }

    fun showDeleteDelivery() {
        when (val current = deliveryViewer.state.value) {
            is DeliveryViewerState.Viewing -> {
                _deliveryAction.value = DeliveryAction.Delete(current.currentDelivery.id)
            }

            else -> {
                // TODO(P2): Error handling ...
            }
        }
    }

    fun cancelDeliveryOperation() {
        _deliveryAction.value = null
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
