package com.julianfortune.glacier

import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.repository.*
import com.julianfortune.glacier.viewModel.DeliveryListViewModel
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.ItemViewModel
import com.julianfortune.glacier.viewModel.NamedEntityViewModel
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
        DeliveryViewModel(get(), get(), get())
    }
    viewModel {
        DeliveryListViewModel(get(), get(), get())
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
