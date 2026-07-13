package com.julianfortune.glacier.ui.feature.delivery.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.core.viewer.DeliveryViewer
import com.julianfortune.glacier.core.viewer.data.DeliveryViewerState
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.ui.common.provider.SupplierOptionsProvider
import com.julianfortune.glacier.ui.feature.delivery.form.data.DeliveryBody
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryHeadlineListViewModel(
    private val deliveryViewer: DeliveryViewer,
    private val deliveryRepository: DeliveryRepository,
    supplierOptionsProvider: SupplierOptionsProvider
) : ViewModel(), SupplierOptionsProvider by supplierOptionsProvider {

    sealed interface UiEvent {
        data class DeliveryCreated(val id: Long) : UiEvent
    }

    private val _uiEventChannel = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: StateFlow<UiEvent?> = _uiEventChannel.receiveAsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val selectedId = deliveryViewer.state.map {
        when (it) {
            DeliveryViewerState.Empty -> null
            DeliveryViewerState.Loading -> null
            is DeliveryViewerState.Viewing -> it.currentDelivery.id
        }
    }

    // TODO(P3): Sorting, default: By receivedDate and then createdDatetime
    // TODO(P5): Filtering, e.g., by time period
    val allDeliveries = deliveryRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onSelect(id: Long) {
        deliveryViewer.view(id)
    }

    fun saveNewDelivery(delivery: DeliveryBody) {
        viewModelScope.launch {
            val result = deliveryRepository.insertDelivery(
                delivery.received,
                delivery.supplierId,
                delivery.taxesCents,
                delivery.feesCents,
            )

            result.map { newId ->
                _uiEventChannel.send(UiEvent.DeliveryCreated(newId))
                deliveryViewer.view(newId)
            }
            // TODO(P3): Error handling
        }
    }
}