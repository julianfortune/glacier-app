package com.julianfortune.glacier.core.viewer

import com.julianfortune.glacier.core.viewer.data.DeliveryViewerState
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
class DefaultDeliveryViewer(
    deliveryRepository: DeliveryRepository,
    coroutineScope: CoroutineScope,
) : DeliveryViewer {

    private val _targetDeliveryId = MutableStateFlow<Long?>(null)

    private val _delivery: Flow<Delivery?> = _targetDeliveryId.flatMapLatest { id ->
        id?.let { deliveryRepository.getDeliveryById(it) } ?: flowOf(null)
    }

    override val state: StateFlow<DeliveryViewerState> = combine(_delivery, _targetDeliveryId) { delivery, targetId ->
        when {
            targetId == null -> DeliveryViewerState.Empty
            delivery != null && delivery.id == targetId -> DeliveryViewerState.Viewing(delivery)
            else -> DeliveryViewerState.Loading
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeliveryViewerState.Empty,
    )

    override fun view(deliveryId: Long) {
        _targetDeliveryId.value = deliveryId
    }

    override fun clear() {
        _targetDeliveryId.value = null
    }
}