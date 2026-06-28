package com.julianfortune.glacier.feature.delivery.headline

import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach

class DeliveryHeadlineListViewModelTest {

    lateinit var viewModel: DeliveryHeadlineListViewModel

    lateinit var deliveryRepository: DeliveryRepository
    lateinit var itemRepository: ItemRepository
    lateinit var supplierRepository: SupplierRepository

    @BeforeEach
    fun setUp() {
        deliveryRepository = mockk(relaxed = true)
        itemRepository = mockk(relaxed = true)
        supplierRepository = mockk(relaxed = true)

        viewModel = DeliveryHeadlineListViewModel(
            deliveryRepository,
            itemRepository,
            supplierRepository,
        )
    }

}