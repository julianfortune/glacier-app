package com.julianfortune.glacier

import com.julianfortune.glacier.data.Database
import com.julianfortune.glacier.data.DatabaseDriverFactory
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.SupplierRepository
import com.julianfortune.glacier.viewModel.CategoryViewModel
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.SupplierViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

val appModule = module {
    single { DatabaseDriverFactory().createDriver() }
    single { Database(get()) }

    single { CategoryRepository(get()) }
    single { SupplierRepository(get()) }
    single { DeliveryRepository(get()) }

    factory { (coroutineScope: CoroutineScope) ->
        // TODO: Use `coroutineScope` ?
        CategoryViewModel(get())
    }
    factory { (coroutineScope: CoroutineScope) ->
        // TODO: Use `coroutineScope` ?
        SupplierViewModel(get())
    }
    factory { (coroutineScope: CoroutineScope) ->
        // TODO: Use `coroutineScope` ?
        DeliveryViewModel(get(), get())
    }
}