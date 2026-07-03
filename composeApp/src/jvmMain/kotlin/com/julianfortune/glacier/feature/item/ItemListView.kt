package com.julianfortune.glacier.feature.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.ui.common.CollectionView
import com.julianfortune.glacier.ui.common.ConfirmDeleteEntityForm
import com.julianfortune.glacier.ui.common.EntityOptionsDropdownMenu
import com.julianfortune.glacier.feature.namedentity.data.EntityOperation
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


// TODO: Proactively check if `Item` is referenced by any Deliveries and prevent deleting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListView(viewModel: ItemViewModel = koinViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    val items by viewModel.items.collectAsState(emptyList())

    val itemOperation by viewModel.itemOperation

    Column {
        CollectionView(
            "Items",
            items,
            null,
            onClickCreateNew = {
                viewModel.showNewItem()
            }
        ) { item, modifier, elevation ->
            ListItem(
                headlineContent = {
                    Text(item.name)
                },
                modifier = modifier.clickable(
                    enabled = true,
                    onClick = {
                        viewModel.showEditItem(item)
                    }
                ),
                tonalElevation = elevation,
                trailingContent = {
                    EntityOptionsDropdownMenu(
                        edit = {
                            viewModel.showEditItem(item)
                        },
                        delete = {
                            viewModel.showDeleteItem(item)
                        },
                    )
                }
            )
        }
    }

    // Modal
    if (itemOperation != null) {
        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                when (itemOperation) {
                    is EntityOperation.CreateNew -> {
                        ItemForm(viewModel, "Create Item", "Create") { name ->
                            coroutineScope.launch {
                                viewModel.saveItem(name)
                                viewModel.dismissItemModal()
                            }
                        }
                    }

                    is EntityOperation.Edit -> {
                        val originalItem = (itemOperation as EntityOperation.Edit).entity

                        ItemForm(viewModel, "Edit Item", "Save", originalItem) { name ->
                            coroutineScope.launch {
                                viewModel.updateItem(originalItem.id, name)
                                viewModel.dismissItemModal()
                            }
                        }
                    }

                    is EntityOperation.Delete -> {
                        val itemId = (itemOperation as EntityOperation.Delete).id
                        ConfirmDeleteEntityForm(
                            itemId,
                            "Delete Item",
                            onCancel = {
                                viewModel.cancelItemOperation()
                            },
                            onConfirm = {
                                coroutineScope.launch {
                                    viewModel.deleteItem(itemId)
                                    viewModel.dismissItemModal()
                                }
                            }
                        )
                    }

                    else -> throw Error("`itemOperation` must not be `null`")
                }
            }
        }
    }
}
