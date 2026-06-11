package com.julianfortune.glacier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.data.domain.Program
import com.julianfortune.glacier.data.domain.PurchasingAccount
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.view.delivery.DeliveriesListDetailView
import com.julianfortune.glacier.view.item.ItemListView
import com.julianfortune.glacier.view.namedentity.NamedEntityListView
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.ItemViewModel
import com.julianfortune.glacier.viewModel.NamedEntityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

enum class NavigationPage(val title: String, val icon: ImageVector) {
    DELIVERIES("Deliveries", Icons.Outlined.LocalShipping),
    ITEMS("Items", Icons.Outlined.FoodBank),
    SUPPLIERS("Suppliers", Icons.Outlined.Storefront),
    PROGRAMS("Programs", Icons.Outlined.Cases),
    PURCHASING_ACCOUNTS("Accounts", Icons.Outlined.AccountBalanceWallet),
    CATEGORIES("Categories", Icons.Outlined.Category),
    REPORTS("Reports", Icons.Outlined.Analytics),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    // TODO: Can this be simplified ..?
    // Probably should look at: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html#using-viewmodel-in-common-code
    val categoryViewModel: NamedEntityViewModel<Category> = koinInject(named("categoryViewModel"))
    val deliveriesViewModel = koinInject<DeliveryViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }
    val itemViewModel = koinInject<ItemViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }
    val programViewModel: NamedEntityViewModel<Program> = koinInject(named("programViewModel"))
    val purchasingAccountViewModel: NamedEntityViewModel<PurchasingAccount> =
        koinInject(named("purchasingAccountViewModel"))
    val supplierViewModel: NamedEntityViewModel<Supplier> = koinInject(named("supplierViewModel"))

    var selectedNavigationItem by remember { mutableStateOf(NavigationPage.DELIVERIES) }

    MaterialTheme(
        colorScheme = darkColorScheme() //  darkColorScheme() or lightColorScheme()
    ) {
        Row {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                NavigationPage.entries.forEach { page ->
                    NavigationRailItem(
                        colors = NavigationRailItemDefaults.colors(),
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        selected = selectedNavigationItem == page,
                        onClick = {
                            selectedNavigationItem = page
                        },
                        icon = {
                            Icon(page.icon, null)
                        },
                        label = { Text(page.title) })
                }
            }
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                when (selectedNavigationItem) {
                    NavigationPage.CATEGORIES -> NamedEntityListView(
                        categoryViewModel,
                        "Categories",
                        "Category"
                    ) { Category(it) }

                    NavigationPage.DELIVERIES -> DeliveriesListDetailView(deliveriesViewModel)
                    NavigationPage.ITEMS -> ItemListView(itemViewModel)
                    NavigationPage.PROGRAMS -> NamedEntityListView(
                        programViewModel,
                        "Programs",
                        "Program"
                    ) { Program(it) }

                    NavigationPage.PURCHASING_ACCOUNTS -> NamedEntityListView(
                        purchasingAccountViewModel,
                        "Accounts",
                        "Account"
                    ) { PurchasingAccount(it) }

                    NavigationPage.SUPPLIERS -> NamedEntityListView(
                        supplierViewModel,
                        "Suppliers",
                        "Supplier"
                    ) { Supplier(it) }

                    else -> Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Not implemented yet!")
                        Text("(Selected: ${selectedNavigationItem.title})")
                    }
                }
            }
        }
    }
}

