package com.julianfortune.glacier

import com.julianfortune.glacier.ui.coordinator.delivery.DefaultDeliveryViewCoordinator
import com.julianfortune.glacier.ui.coordinator.delivery.DeliveryViewCoordinator
import com.julianfortune.glacier.data.repository.BasicReportRepository
import com.julianfortune.glacier.data.repository.CategoryRepository
import com.julianfortune.glacier.data.repository.DeliveryRepository
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.data.repository.ProgramRepository
import com.julianfortune.glacier.data.repository.PurchasingAccountRepository
import com.julianfortune.glacier.data.repository.SupplierRepository
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.ui.coordinator.report.DefaultReportViewCoordinator
import com.julianfortune.glacier.ui.coordinator.report.ReportViewCoordinator
import com.julianfortune.glacier.ui.delegate.CategoryOptionsProvider
import com.julianfortune.glacier.ui.delegate.DefaultCategoryOptionsProvider
import com.julianfortune.glacier.ui.delegate.DefaultItemOptionsProvider
import com.julianfortune.glacier.ui.delegate.DefaultProgramOptionsProvider
import com.julianfortune.glacier.ui.delegate.DefaultPurchasingAccountOptionsProvider
import com.julianfortune.glacier.ui.delegate.DefaultSupplierOptionsProvider
import com.julianfortune.glacier.ui.delegate.ItemOptionsProvider
import com.julianfortune.glacier.ui.delegate.ProgramOptionsProvider
import com.julianfortune.glacier.ui.delegate.PurchasingAccountOptionsProvider
import com.julianfortune.glacier.ui.delegate.SupplierOptionsProvider
import com.julianfortune.glacier.ui.feature.delivery.detail.DeliveryDetailViewModel
import com.julianfortune.glacier.ui.feature.delivery.list.DeliveryHeadlineListViewModel
import com.julianfortune.glacier.ui.feature.entry.table.EntryTableViewModel
import com.julianfortune.glacier.ui.feature.report.list.ReportHeadlineListViewModel
import com.julianfortune.glacier.ui.page.item.ItemsPageViewModel
import com.julianfortune.glacier.ui.page.namedentity.NamedEntityPageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { Database(get()) }

    // Repositories
    single { BasicReportRepository(get()) }
    single { DeliveryRepository(get()) }
    single { CategoryRepository(get()) }
    single { ItemRepository(get()) }
    single { SupplierRepository(get()) }
    single { ProgramRepository(get()) }
    single { PurchasingAccountRepository(get()) }

    // View coordinators
    single<DeliveryViewCoordinator> {
        DefaultDeliveryViewCoordinator(
            get(),
            CoroutineScope(Dispatchers.Default),
        )
    }
    single<ReportViewCoordinator> {
        DefaultReportViewCoordinator(
            get(),
            CoroutineScope(Dispatchers.Default),
        )
    }

    // ViewModel delegates
    single<CategoryOptionsProvider> {
        DefaultCategoryOptionsProvider(
            categoryRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<ItemOptionsProvider> {
        DefaultItemOptionsProvider(
            itemRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<ProgramOptionsProvider> {
        DefaultProgramOptionsProvider(
            programRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<PurchasingAccountOptionsProvider> {
        DefaultPurchasingAccountOptionsProvider(
            purchasingAccountRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }
    single<SupplierOptionsProvider> {
        DefaultSupplierOptionsProvider(
            supplierRepository = get(),
            scope = CoroutineScope(Dispatchers.Default)
        )
    }

    // ViewModels
    viewModel(named("categoryViewModel")) {
        NamedEntityPageViewModel(get<CategoryRepository>())
    }
    viewModel {
        EntryTableViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        DeliveryDetailViewModel(
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        DeliveryHeadlineListViewModel(
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        ItemsPageViewModel(get(), get())
    }
    viewModel(named("programViewModel")) {
        NamedEntityPageViewModel(get<ProgramRepository>())
    }
    viewModel(named("purchasingAccountViewModel")) {
        NamedEntityPageViewModel(get<PurchasingAccountRepository>())
    }
    viewModel {
        ReportHeadlineListViewModel(get(), get())
    }
    viewModel(named("supplierViewModel")) {
        NamedEntityPageViewModel(get<SupplierRepository>())
    }

}
