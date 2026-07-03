package com.julianfortune.glacier.feature.delivery.headline.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.feature.delivery.editor.DeliveryEditorStateHolder
import com.julianfortune.glacier.feature.delivery.editor.DeliveryEditorState
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NewDeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val supplierRepository: SupplierRepository,
) : ViewModel() {

    data class DeliveryCreated(val id: Long)

    val stateHolder = DeliveryEditorStateHolder()
    val uiState: StateFlow<DeliveryEditorState> = stateHolder.state

    private val _channel = Channel<DeliveryCreated>(Channel.BUFFERED)
    val deliveryCreated = _channel.receiveAsFlow()

    init {
        viewModelScope.launch {
            supplierRepository.getAll().collect { supplierList ->
                val suppliers = supplierList.map { Option(it.id, it.name) }
                stateHolder.updateSuppliers(suppliers)
            }
        }
    }

    fun createDelivery() {
        // TODO(P2): Error handling
        val validated = uiState.value.validated ?: throw IllegalArgumentException(
            "`validated` must be defined to process the delivery creation"
        )

        viewModelScope.launch {
            deliveryRepository.insertDelivery(
                validated.receivedDate,
                validated.supplierId,
                validated.taxesCents,
                validated.feesCents,
            ).map { newDeliveryId ->
                // Emit event on success
                _channel.send(DeliveryCreated(newDeliveryId))
            }
            // TODO: Handle failure
        }
    }

}