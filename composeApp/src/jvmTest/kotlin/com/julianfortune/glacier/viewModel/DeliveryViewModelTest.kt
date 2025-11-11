package com.julianfortune.glacier.viewModel

import com.julianfortune.glacier.repository.DeliveryRepository
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach

class DeliveryViewModelTest {

    lateinit var viewModel: DeliveryViewModel

    lateinit var deliveryRepository: DeliveryRepository

    @BeforeEach
    fun setUp() {
        deliveryRepository = mockk(relaxed = true)

        viewModel = DeliveryViewModel(deliveryRepository)
    }

}