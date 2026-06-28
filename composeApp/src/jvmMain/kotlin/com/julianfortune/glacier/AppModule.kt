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
        val categoryRepository: CategoryRepository = get()
        NamedEntityViewModel(categoryRepository)
    }
    viewModel {
        DeliveryViewModel(
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        DeliveryListViewModel(get(), get(), get())
    }
    viewModel {
        ItemViewModel(get())
    }
    viewModel(named("programViewModel")) {
        val programRepository: ProgramRepository = get()
        NamedEntityViewModel(programRepository)
    }
    viewModel(named("purchasingAccountViewModel")) {
        val purchasingAccountRepository: PurchasingAccountRepository = get()
        NamedEntityViewModel(purchasingAccountRepository)
    }
    viewModel(named("supplierViewModel")) {
        val supplierRepository: SupplierRepository = get()
        NamedEntityViewModel(supplierRepository)
    }

}
