package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.CostStatus
import com.julianfortune.glacier.repository.DeliveryEntryRepository
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

class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val deliveryEntryRepository: DeliveryEntryRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    // TODO: Sorting, default: By receivedDate and then createdDatetime
    // TODO (enhancement): Filtering, e.g., by time period
    val deliveries = deliveryRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun saveNewDelivery(delivery: Delivery) {
        val newDeliveryId = insertDelivery(delivery.date, delivery.supplierId, delivery.taxesCents, delivery.feesCents)
        delivery.entries.forEach { entry ->
            insertDeliveryEntry(newDeliveryId, entry)
        }

        // TODO: Insert other associated data (DeliveryEntry* tables)
    }

    private suspend fun insertDelivery(
        date: LocalDate,
        supplierId: Long,
        taxesCents: Long,
        feesCents: Long,
    ): Long {
        val now = Instant.now()

        return deliveryRepository.insert(
            date.toString(),
            supplierId,
            taxesCents,
            feesCents,
            now.toString(),
            now.toString(),
        )
    }

    private suspend fun insertDeliveryEntry(deliveryId: Long, entry: DeliveryEntry) {
        deliveryEntryRepository.insert(
            deliveryId,
            entry.itemId,
            entry.itemCount,
            entry.costStatus,
            entry.itemCostCents,
            entry.aggregate?.label,
            entry.aggregate?.count,
        )
    }

    // TODO: May want to set this up using flows...? Make it reactive so that changing the currently selected
    // delivery automatically fetches and updates this list...?
    fun getDeliveryEntries(deliveryId: Long): List<DeliveryEntry> {
        return deliveryEntryRepository
            .getAllByDeliveryId(deliveryId)
            .map { entry ->
                // TODO (ASAP): !!! Move all the mapping (and timestamp management) into the repositories
                // TODO (ASAP): Define the 'business' / VM level entities in a common spot
                DeliveryEntry(
                    entry.itemId,
                    entry.itemCount,
                    CostStatus.NO_COST, // entry.costStatus
                    entry.itemCostCents,
                    if (entry.aggregateLabel != null && entry.aggregateCount != null) {
                        DeliveryEntry.Aggregate(
                            entry.aggregateLabel,
                            entry.aggregateCount
                        )
                    } else null,
                )
            }
    }
    // TODO (way later): Adding other entities (e.g., Items, Categories, ...)
}
