package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.DeliveryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
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
    val deliveries = deliveryRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun saveNewDelivery(delivery: Delivery) {
        insertDelivery(delivery.date, delivery.supplierId, delivery.taxesCents, delivery.feesCents)
    }

    private suspend fun insertDelivery(
        date: LocalDate,
        supplierId: Long,
        taxesCents: Long,
        feesCents: Long,
    ): Long {
        return deliveryRepository.insert(
            date.toString(),
            supplierId,
            taxesCents,
            feesCents,
        )
    }

    // TODO: May want to set this up using flows...? Make it reactive so that changing the currently selected
    // delivery automatically fetches and updates this list...?
    fun getDeliveryEntries(deliveryId: Long): List<DeliveryEntry> {
        return TODO()
    }

    // TODO (way later): Adding other entities (e.g., Items, Categories, ...)
}
