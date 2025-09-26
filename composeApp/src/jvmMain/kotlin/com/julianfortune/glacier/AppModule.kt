package com.julianfortune.glacier

import com.julianfortune.glacier.data.Database
import com.julianfortune.glacier.data.DatabaseDriverFactory
import com.julianfortune.glacier.repository.CategoryRepository
import com.julianfortune.glacier.viewModel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

val appModule = module {
    single { DatabaseDriverFactory().createDriver() }
    single { Database(get()) }
    single { CategoryRepository(get()) }
    factory { (coroutineScope: CoroutineScope) ->
        // TODO: Use `coroutineScope` ?
        CategoryViewModel(get())
    }
}