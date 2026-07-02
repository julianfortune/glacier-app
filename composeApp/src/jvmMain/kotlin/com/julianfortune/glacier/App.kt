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
import com.julianfortune.glacier.feature.delivery.DeliveriesScreen
import com.julianfortune.glacier.feature.item.ItemListView
import com.julianfortune.glacier.feature.namedentity.NamedEntityListView
import org.koin.compose.viewmodel.koinViewModel
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
                    NavigationPage.CATEGORIES -> NamedEntityListView<Category>(
                        koinViewModel(named("categoryViewModel")),
                        "Categories",
                        "Category"
                    )

                    NavigationPage.DELIVERIES -> DeliveriesScreen()
                    NavigationPage.ITEMS -> ItemListView()
                    NavigationPage.PROGRAMS -> NamedEntityListView<Program>(
                        koinViewModel(named("programViewModel")),
                        "Programs",
                        "Program"
                    )

                    NavigationPage.PURCHASING_ACCOUNTS -> NamedEntityListView<PurchasingAccount>(
                        koinViewModel(named("purchasingAccountViewModel")),
                        "Accounts",
                        "Account"
                    )

                    NavigationPage.SUPPLIERS -> NamedEntityListView<Supplier>(
                        koinViewModel(named("supplierViewModel")),
                        "Suppliers",
                        "Supplier"
                    )

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

