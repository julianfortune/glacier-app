package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.DeliveryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    // TODO: Inserting new Delivery (including the entries and associated data)
    // TODO: (private) Adding new entry in delivery (including the program and purchasing account links)

    fun addItem(name: String) { // more ...
        // TODO ...
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryRepository.insert(null, name)
        }
    }

    fun updateItem(todo: String) { // ...
        // TODO ...
    }

    fun updateCategory(newName: String) {
        // TODO ...
    }

    // TODO: (?) Add/update supplier
    // TODO: Add/update



    // Also ... Adding new other entities ..?
}
