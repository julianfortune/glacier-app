package com.julianfortune.glacier

import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.db.DatabaseDriverFactory
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.repository.DeliveryRepository
import com.julianfortune.glacier.repository.SupplierRepository
import com.julianfortune.glacier.viewModel.CategoryViewModel
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.SupplierViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val appModule = module {
    single {
        runBlocking { // TODO: Figure out the right way to do this
            DatabaseDriverFactory().createDriver()
        }
    }
    single { Database(get()) }

    single { SupplierRepository(get()) }
    single { DeliveryRepository(get()) }
    single { CategoryRepository(get()) }

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
        DeliveryViewModel(get())
    }
}