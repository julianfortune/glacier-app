package com.julianfortune.glacier.viewModel

import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.repository.SupplierRepository
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach

class DeliveryViewModelTest {

    lateinit var viewModel: DeliveryViewModel

    lateinit var deliveryRepository: DeliveryRepository
    lateinit var itemRepository: ItemRepository
    lateinit var supplierRepository: SupplierRepository

    @BeforeEach
    fun setUp() {
        deliveryRepository = mockk(relaxed = true)
        itemRepository = mockk(relaxed = true)
        supplierRepository = mockk(relaxed = true)

        viewModel = DeliveryViewModel(
            deliveryRepository,
            itemRepository,
            supplierRepository,
        )
    }

}