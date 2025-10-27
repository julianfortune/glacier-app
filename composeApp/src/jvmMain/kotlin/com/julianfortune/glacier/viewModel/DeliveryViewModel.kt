package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.DeliveryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate


data class DeliveryEntry(
    val itemId: Long,

    )

data class Delivery(
    val date: LocalDate,
    val supplierId: Long,
    val taxesCents: Long,
    val feesCents: Long,
    val entries: List<DeliveryEntry>
)

class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    // TODO: Sorting, default: By receivedDate and then createdDatetime
    // (enhancement) TODO: Filtering, e.g., by time period
    val deliveries = deliveryRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun saveNewDelivery(delivery: Delivery) {
        insertDelivery(delivery.date, delivery.supplierId, delivery.taxesCents, delivery.feesCents)
    }

    private fun insertDelivery(
        date: LocalDate,
        supplierId: Long,
        taxesCents: Long,
        feesCents: Long,
    ) {
        val now = Instant.now()

        println(">>> ${date.toString()}")

        deliveryRepository.insert(
            date.toString(),
            supplierId,
            taxesCents,
            feesCents,
            now.toString(),
            now.toString(),
        )
    }

    // TODO: Getting list of entries for a delivery

    // TODO: Inserting new Delivery (including the entries and associated data)
    // TODO: (private) Adding new entry in delivery (including the program and purchasing account links)

    fun addItem(name: String) { // more ...
        // TODO ...
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryRepository.insert(name)
        }
    }

    fun updateItem(todo: String) { // ...
        // TODO ...
    }

    fun updateCategory(newName: String) {
        // TODO ...
    }

    // TODO (way later): Adding other entities ...
}
