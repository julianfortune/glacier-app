package com.julianfortune.glacier.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.repository.SupplierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val itemRepository: ItemRepository,
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val _currentDeliveryId = MutableStateFlow<Long?>(null)
    private val _newDeliveryDialogIsVisible = mutableStateOf(false)

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

    val allSuppliers: StateFlow<List<Entity<Supplier>>> =
        supplierRepository
            .getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    // TODO: Implement flow logic for the current delivery details to show
//    val selectedDeliveryDetail: StateFlow<Entity<DeliveryDetail>> = TODO()

    val newDeliveryDialogIsVisible: State<Boolean> = _newDeliveryDialogIsVisible

    fun selectDelivery(deliveryId: Long?) {
        _currentDeliveryId.value = deliveryId
    }

    suspend fun save(delivery: DeliveryDetail): Long {
        return deliveryRepository.insert(delivery).also { newId ->
            selectDelivery(newId)
        }
    }

    suspend fun update(delivery: Entity<DeliveryDetail>) {
        TODO()
    }

    fun showNewDelivery() {
        _newDeliveryDialogIsVisible.value = true
    }

    fun dismissNewDelivery() {
        _newDeliveryDialogIsVisible.value = false
    }

}
