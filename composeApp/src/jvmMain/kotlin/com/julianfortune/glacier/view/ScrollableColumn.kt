package com.julianfortune.glacier.view

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

data class Item(
    val name: String,
    val onClick: () -> Unit,
    val isSelected: Boolean = false
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollableColumn(items: List<Item>) {
    if (items.isEmpty()) {
        Text("No items to display!")
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val listScrollState = rememberLazyListState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listScrollState,
            ) {
                items(items) { i ->
                    // Use colors to highlight selected item
                    val colors = if (i.isSelected) {
                        ListItemDefaults.colors(
                            headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    } else ListItemDefaults.colors()

                    ListItem(
                        headlineContent = {
                            Text(i.name)
                        },
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable {
                                i.onClick()
                            },
                        colors = colors,
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(2.dp),
                adapter = rememberScrollbarAdapter(listScrollState)
            )
        }
    }
}
