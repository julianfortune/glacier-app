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
import com.julianfortune.glacier.view.category.CategoryListView
import com.julianfortune.glacier.view.delivery.DeliveriesListDetailView
import com.julianfortune.glacier.view.item.ItemListView
import com.julianfortune.glacier.view.supplier.SupplierListView
import com.julianfortune.glacier.viewModel.CategoryViewModel
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.ItemViewModel
import com.julianfortune.glacier.viewModel.SupplierViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

enum class NavigationPage(val title: String, val icon: ImageVector) {
    DELIVERIES("Deliveries", Icons.Outlined.LocalShipping),
    ITEMS("Items", Icons.Outlined.FoodBank),
    SUPPLIERS("Suppliers", Icons.Outlined.Storefront),
    PROGRAMS("Programs", Icons.Outlined.Cases),
    SUBACCOUNTS("Accounts", Icons.Outlined.AccountBalanceWallet),
    CATEGORIES("Categories", Icons.Outlined.Category),
    REPORTS("Reports", Icons.Outlined.Analytics),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    // TODO: Can this be simplified ..?
    // Probably should look at: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html#using-viewmodel-in-common-code
    val categoryViewModel = koinInject<CategoryViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }
    val deliveriesViewModel = koinInject<DeliveryViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }
    val itemViewModel = koinInject<ItemViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }
    val supplierViewModel = koinInject<SupplierViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }


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
                    NavigationPage.CATEGORIES -> CategoryListView(categoryViewModel)
                    NavigationPage.DELIVERIES -> DeliveriesListDetailView(deliveriesViewModel)
                     NavigationPage.ITEMS -> ItemListView(itemViewModel)
                     NavigationPage.SUPPLIERS -> SupplierListView(supplierViewModel)
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

