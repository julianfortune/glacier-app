package com.julianfortune.glacier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.julianfortune.glacier.view.Item
import com.julianfortune.glacier.view.ScrollableColumn
import com.julianfortune.glacier.view.delivery.DeliveriesPane
import com.julianfortune.glacier.viewModel.CategoryViewModel
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.SupplierViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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
    val supplierViewModel = koinInject<SupplierViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }
    val deliveriesViewModel = koinInject<DeliveryViewModel> {
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
                    NavigationPage.CATEGORIES -> CategoryList(categoryViewModel)
                    NavigationPage.SUPPLIERS -> SupplierList(supplierViewModel)
                    NavigationPage.DELIVERIES -> DeliveriesPane(deliveriesViewModel)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryList(viewModel: CategoryViewModel) {
    Column {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Categories", fontSize = (1.25).em)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(32.dp) // Match the buttons
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = MaterialTheme.shapes.extraSmall, // Match the buttons
                            )
                    ) {
                        BasicTextField(
                            modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp),
                            value = "",
                            onValueChange = { },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Search,
                                        null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    innerTextField()
                                }
                            }
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = { },
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    ) {
                        Text("New")
                    }
                }
            }
        }
        HorizontalDivider(thickness = 1.dp)
        ScrollableColumn(
            viewModel.categories.map { categories ->
                categories.map {
                    Item(
                        it.data.name, onClick = {
                            println("Deleting category: $it ...")
                            viewModel.deleteCategory(it)
                        })
                }
            }.collectAsState(emptyList())
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierList(viewModel: SupplierViewModel) {
    Column {
        ScrollableColumn(
            viewModel.suppliers.map { suppliers ->
                suppliers.map {
                    Item(
                        it.data.name, onClick = {
                            println("Deleting supplier: ${it.id} ...")
                            viewModel.deleteSupplier(it.id)
                        })
                }
            }.collectAsState(emptyList())
        )
    }
}