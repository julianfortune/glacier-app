package com.julianfortune.glacier.feature.delivery.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.DeliveryHeadline
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.feature.delivery.common.data.DeliveryBody
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryHeadlineListViewModel(
    private val deliveryRepository: DeliveryRepository,
    itemRepository: ItemRepository,
    supplierRepository: SupplierRepository
) : ViewModel() {
    // TODO(P3): Sorting, default: By receivedDate and then createdDatetime
    // TODO(P5): Filtering, e.g., by time period
    val allDeliveries = deliveryRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
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

    fun saveNewDelivery(delivery: DeliveryBody) {
        viewModelScope.launch {
            deliveryRepository.insertDelivery(
                delivery.received,
                delivery.supplierId,
                delivery.taxesCents,
                delivery.feesCents,
            )
        }
    }
}