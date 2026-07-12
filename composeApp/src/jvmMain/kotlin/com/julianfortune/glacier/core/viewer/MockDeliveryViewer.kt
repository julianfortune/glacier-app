package com.julianfortune.glacier.core.viewer

import com.julianfortune.glacier.core.viewer.data.DeliveryViewerState
import com.julianfortune.glacier.data.common.EntityMetadata
import com.julianfortune.glacier.data.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.LocalDate

class MockDeliveryViewer(state: DeliveryViewerState?) : DeliveryViewer {

    private val _state = MutableStateFlow(
        state ?: DeliveryViewerState.Viewing(
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