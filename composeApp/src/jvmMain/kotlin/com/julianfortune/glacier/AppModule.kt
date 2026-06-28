package com.julianfortune.glacier

import com.julianfortune.glacier.data.repository.CategoryRepository
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.ProgramRepository
import com.julianfortune.glacier.data.repository.PurchasingAccountRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.feature.delivery.headline.DeliveryHeadlineListViewModel
import com.julianfortune.glacier.feature.delivery.detail.DeliveryDetailViewModel
import com.julianfortune.glacier.feature.item.ItemViewModel
import com.julianfortune.glacier.feature.namedentity.NamedEntityViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { Database(get()) }

    single { CategoryRepository(get()) }
    single { DeliveryRepository(get()) }
    single { ItemRepository(get()) }
    single { SupplierRepository(get()) }
    single { ProgramRepository(get()) }
    single { PurchasingAccountRepository(get()) }

    viewModel(named("categoryViewModel")) {
        NamedEntityViewModel(get<CategoryRepository>())
    }
    viewModel {
        DeliveryDetailViewModel(get(), get(), get())
    }
    viewModel {
        DeliveryHeadlineListViewModel(get(), get(), get())
    }
    viewModel {
        ItemViewModel(get())
    }
    viewModel(named("programViewModel")) {
        NamedEntityViewModel(get<ProgramRepository>())
    }
    viewModel(named("purchasingAccountViewModel")) {
        NamedEntityViewModel(get<PurchasingAccountRepository>())
    }
    viewModel(named("supplierViewModel")) {
        NamedEntityViewModel(get<SupplierRepository>())
    }

}
