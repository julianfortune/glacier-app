package com.julianfortune.glacier.ui.coordinator.delivery

import com.julianfortune.glacier.ui.coordinator.delivery.data.DeliveryViewState
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.repository.DeliveryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultDeliveryViewCoordinator(
    deliveryRepository: DeliveryRepository,
    coroutineScope: CoroutineScope,
) : DeliveryViewCoordinator {

    private val _targetDeliveryId = MutableStateFlow<Long?>(null)

    private val _delivery: Flow<Delivery?> = _targetDeliveryId.flatMapLatest { id ->
        id?.let { deliveryRepository.getDeliveryById(it) } ?: flowOf(null)
    }

    override val state: StateFlow<DeliveryViewState> = combine(_delivery, _targetDeliveryId) { delivery, targetId ->
        when {
            targetId == null -> DeliveryViewState.Empty
            delivery != null && delivery.id == targetId -> DeliveryViewState.Viewing(delivery)
            else -> DeliveryViewState.Loading
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeliveryViewState.Empty,
    )

    override fun view(deliveryId: Long) {
        _targetDeliveryId.value = deliveryId
    }

    override fun clear() {
        _targetDeliveryId.value = null
    }
}