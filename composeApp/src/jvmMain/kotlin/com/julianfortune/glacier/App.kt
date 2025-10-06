package com.julianfortune.glacier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.Category
import com.julianfortune.glacier.viewModel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.ui.tooling.preview.Preview
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
@Preview
fun App() {
    // Probably should look at: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html#using-viewmodel-in-common-code
    val categoryViewModel = koinInject<CategoryViewModel> {
        parametersOf(CoroutineScope(Dispatchers.Main))
    }
    var selectedNavigationItem by remember { mutableStateOf(NavigationPage.DELIVERIES) }

    MaterialTheme {
        Row {
            NavigationRail(
                containerColor = NavigationRailDefaults.ContainerColor
            ) {
                NavigationPage.entries.forEach { page ->
                    NavigationRailItem(
                        selected = selectedNavigationItem == page,
                        onClick = {
                            selectedNavigationItem = page
                        },
                        icon = {
                            Icon(page.icon, null)
                        },
                        label = { Text(page.title) }
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                when (selectedNavigationItem) {
                    NavigationPage.CATEGORIES -> CategoryList(categoryViewModel)
                    else -> Text("Selected: ${selectedNavigationItem.title}")
                }
            }
        }
    }
}

@Composable
fun CategoryList(viewModel: CategoryViewModel) {
    val categories by viewModel.categories.collectAsState()

    if (categories.isEmpty()) {
        Text("No categories!")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                TaskItem(category)
            }
        }
    }
}

@Composable
fun TaskItem(category: Category) {
    SelectionContainer { Text(category.name) }
}
