package com.julianfortune.glacier

import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.repository.*
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.ItemViewModel
import com.julianfortune.glacier.viewModel.NamedEntityViewModel
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

    factory(named("categoryViewModel")) {
        val categoryRepository: CategoryRepository = get()
        NamedEntityViewModel(categoryRepository)
    }
    factory {
        DeliveryViewModel(get(), get(), get())
    }
    factory {
        ItemViewModel(get())
    }
    factory(named("programViewModel")) {
        val programRepository: ProgramRepository = get()
        NamedEntityViewModel(programRepository)
    }
    factory(named("purchasingAccountViewModel")) {
        val purchasingAccountRepository: PurchasingAccountRepository = get()
        NamedEntityViewModel(purchasingAccountRepository)
    }
    factory(named("supplierViewModel")) {
        val supplierRepository: SupplierRepository = get()
        NamedEntityViewModel(supplierRepository)
    }
}
