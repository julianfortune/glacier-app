package com.julianfortune.glacier.feature.delivery.page.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.SupplierRepository
import com.julianfortune.glacier.feature.delivery.editor.DeliveryEditorState
import com.julianfortune.glacier.feature.delivery.editor.DeliveryEditorStateHolder
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


// TODO: This probably doesn't need to exist ...
@OptIn(ExperimentalCoroutinesApi::class)
class EditDeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val supplierRepository: SupplierRepository,
) : ViewModel() {

    data object DeliveryUpdated

    val stateHolder = DeliveryEditorStateHolder()
    val uiState: StateFlow<DeliveryEditorState> = stateHolder.state

    private val _channel = Channel<DeliveryUpdated>(Channel.BUFFERED)
    val deliveryUpdated = _channel.receiveAsFlow()

    init {
        viewModelScope.launch {
            supplierRepository.getAll().collect { supplierList ->
                val suppliers = supplierList.map { Option(it.id, it.data.name) }
                stateHolder.updateSuppliers(suppliers)
            }
        }
    }

    fun updateDelivery(deliveryId: Long) {
        // TODO(P2): Error handling
        val validated = uiState.value.validated ?: throw IllegalArgumentException(
            "`validated` must be defined to process the delivery update"
        )

        viewModelScope.launch {
            val delivery = DeliveryDetail(
                validated.receivedDate,
                validated.supplierId,
                validated.taxesCents,
                validated.feesCents,
                emptyList()
            )
            // Don't update the entries at all
            deliveryRepository.updateDetailsOnly(Entity(deliveryId, delivery))

            _channel.send(DeliveryUpdated)
        }
    }

}