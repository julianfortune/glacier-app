package com.julianfortune.glacier.viewModel

import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.CostStatus
import com.julianfortune.glacier.repository.DeliveryEntryRepository
import com.julianfortune.glacier.repository.DeliveryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

class DeliveryViewModelTest {

    lateinit var viewModel: DeliveryViewModel

    lateinit var deliveryRepository: DeliveryRepository
    lateinit var deliveryEntryRepository: DeliveryEntryRepository
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        deliveryRepository = mockk(relaxed = true)
        deliveryEntryRepository = mockk(relaxed = true)
        categoryRepository = mockk(relaxed = true)

        viewModel = DeliveryViewModel(deliveryRepository, deliveryEntryRepository, categoryRepository)
    }

    @Test
    fun saveNewDelivery() {
        // GIVEN
        val delivery = Delivery(
            LocalDate.of(2025, 10, 26),
            45L,
            2000L,
            1500L,
            listOf(
                DeliveryEntry(
                    3,
                    10,
                    CostStatus.NO_COST,
                    1500L,
                    null,
                )
            )
        )
        every {
            deliveryRepository.insert(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns 6L

        // WHEN
        viewModel.saveNewDelivery(delivery)

        // THEN
        verify {
            deliveryRepository.insert(
                "2025-10-26",
                45L,
                2000L,
                1500L,
                any(),
                any()
            )
            deliveryEntryRepository.insert(
                6L,
                3,
                10,
                CostStatus.NO_COST,
                1500L,
                null,
                null,
            )
        }

        // TODO: Check other repositories invoked correctly ...
    }

}