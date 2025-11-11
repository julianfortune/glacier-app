package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.repository.DeliveryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate


data class DeliveryEntry(
    val itemId: Long,
    val itemCount: Long,
    val costStatus: CostStatus,
    val itemCostCents: Long,
    val aggregate: Aggregate?,
) {
    data class Aggregate(
        val label: String,
        val count: Long,
    )
}

data class Delivery(
    val date: LocalDate,
    val supplierId: Long,
    val taxesCents: Long,
    val feesCents: Long,
    val entries: List<DeliveryEntry>
)

class DeliveryViewModel(private val deliveryRepository: DeliveryRepository) : ViewModel() {

    // TODO: Sorting, default: By receivedDate and then createdDatetime
    // TODO (enhancement): Filtering, e.g., by time period
    val allDeliveries = deliveryRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun save(delivery: DeliveryDetail): Long {
        return deliveryRepository.insert(delivery)
    }

    suspend fun update(delivery: Entity<DeliveryDetail>) {
        TODO()
    }

    // TODO: May want to set this up using flows...?
    //       I.e., Make it reactive so that changing the currently selected delivery automatically fetches
    //       and updates this list...?
    fun getDelivery(deliveryId: Long): DeliveryDetail {
        return TODO()
    }

}
