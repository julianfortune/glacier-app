package com.julianfortune.glacier.feature.delivery.page
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Fruit(val name: String, val color: String)

val fruitList = listOf(
    Fruit("Apple", "Red"),
    Fruit("Apricot", "Orange"),
    Fruit("Avocado", "Green"),
    Fruit("Banana", "Yellow"),
    Fruit("Blackberry", "Black"),
    Fruit("Blueberry", "Blue"),
    Fruit("Cherry", "Red"),
    Fruit("Coconut", "Brown"),
    Fruit("Cranberry", "Red"),
    Fruit("Durian", "Yellow"),
    Fruit("Dark Plum", "Purple"),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickyHeaderList() {
    val gutterWidth = 24.dp

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = gutterWidth)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Page Title",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Text(
                        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp, top = 0.dp, 16.dp, bottom = 24.dp)
                    )
                }
            }

            stickyHeader {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = gutterWidth)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fruits",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        IconButton(onClick = { /* Action */ }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Fruit",
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Name",
                        )
                        Text(
                            text = "Color",
                        )
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }
            }

            items(fruitList) { fruit ->
                Column(modifier = Modifier.padding(horizontal = gutterWidth)) {
                    ListItem(
                        headlineContent = {
                            Text(text = fruit.name)
                        },
                        trailingContent = {
                            Text(
                                text = fruit.color,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }
            }

            item {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = gutterWidth)) {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp, top = 24.dp, 16.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp, top = 24.dp, 16.dp, bottom = 8.dp)
                    )
                }
            }
        }
    }
}