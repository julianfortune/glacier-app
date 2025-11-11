package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.repository.DeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DeliveryViewModel(private val deliveryRepository: DeliveryRepository) : ViewModel() {

    private val currentDeliveryId = MutableStateFlow<Long?>(null)

    // TODO: Sorting, default: By receivedDate and then createdDatetime
    // TODO (enhancement): Filtering, e.g., by time period
    val allDeliveries: StateFlow<List<Entity<DeliveryHeadline>>> =
        deliveryRepository
            .getAllAsHeadlines()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    // TODO: Implement flow logic for the current delivery details to show
    val selectedDeliveryDetail: StateFlow<Entity<DeliveryDetail>> = TODO()

    fun selectDelivery(deliveryId: Long?) {
        currentDeliveryId.value = deliveryId
    }

    suspend fun save(delivery: DeliveryDetail): Long {
        return deliveryRepository.insert(delivery).also { newId ->
            selectDelivery(newId)
        }
    }

    suspend fun update(delivery: Entity<DeliveryDetail>) {
        TODO()
    }

}
