package com.julianfortune.glacier.ui.coordinator.delivery

import com.julianfortune.glacier.data.common.EntityMetadata
import com.julianfortune.glacier.data.domain.*
import com.julianfortune.glacier.ui.coordinator.delivery.data.DeliveryViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.LocalDate

class MockDeliveryViewCoordinator(state: DeliveryViewState?) : DeliveryViewCoordinator {

    private val _state = MutableStateFlow(
        state ?: DeliveryViewState.Viewing(
            Delivery(
                1,
                LocalDate.now(),
                Supplier(1, "Example Supplier"),
                null,
                null,
                listOf(
                    Delivery.Entry(
                        1,
                        ItemHeadline(1, "Lettuce"),
                        3,
                        Weight.ofImperial(10, 0f),
                        CostStatus.PURCHASED,
                        2400,
                        null,
                        null,
                        null,
                        null,
                        EntityMetadata(Instant.now(), Instant.now())
                    )
                ),
                EntityMetadata(Instant.now(), Instant.now()),
            )
        )
    )

    override val state = _state.asStateFlow()

    override fun view(deliveryId: Long) {}

    override fun clear() {}
}