package com.julianfortune.glacier.viewModel

import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.DeliveryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate

class DeliveryViewModelTest {

    lateinit var viewModel: DeliveryViewModel

    lateinit var deliveryRepository: DeliveryRepository
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        deliveryRepository = mock()
        categoryRepository = mock()

        viewModel = DeliveryViewModel(deliveryRepository, categoryRepository)
    }

    @Test
    fun saveNewDelivery() {
        // GIVEN
        val delivery = Delivery(
            LocalDate.of(2025, 10, 26),
            45L,
            2000L,
            1500L,
            emptyList()
        )

        // WHEN
        viewModel.saveNewDelivery(delivery)

        // THEN

        // TODO (ASAP): Switch to MockK or look into making mockito play nicely with kotlin

        verify(deliveryRepository).insert(
            "2025-10-26",
            45L,
            2000L,
            1500L,
            any(),
            any()
        )

        // TODO: Check DeliveryEntry and other repositories invoked correctly ...
    }

}