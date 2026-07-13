package com.julianfortune.glacier.ui.page.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.julianfortune.glacier.ui.common.component.ConfirmDeleteEntityForm
import com.julianfortune.glacier.ui.common.component.EntityOptionsDropdownMenu
import com.julianfortune.glacier.ui.common.foundation.Dialog
import com.julianfortune.glacier.ui.common.layout.Collection
import com.julianfortune.glacier.ui.page.item.data.ItemBody
import com.julianfortune.glacier.ui.page.item.ui.ItemForm
import com.julianfortune.glacier.ui.page.namedentity.data.EntityOperation
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsPage(viewModel: ItemsPageViewModel = koinViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    // TODO: Turn this into rich state object
    //  AND Proactively check if `Item` is referenced by any Deliveries to disable deletion
    val items by viewModel.items.collectAsState(emptyList())
    val categoryOptions by viewModel.categoryOptions.collectAsState(emptyList())

    val itemOperation by viewModel.itemOperation

    Column {
        Collection(
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

    // Create/edit item modal
    if (itemOperation != null) {
        Dialog {
            when (itemOperation) {
                is EntityOperation.CreateNew -> {
                    ItemForm(
                        categoryOptions,
                        "Create Item",
                        "Create",
                        onCancel = viewModel::cancelItemOperation,
                    ) { body ->
                        coroutineScope.launch {
                            viewModel.saveItem(body)
                            viewModel.dismissItemModal()
                        }
                    }
                }

                is EntityOperation.Edit -> {
                    val originalItem = (itemOperation as EntityOperation.Edit).entity
                    val itemBody = ItemBody(originalItem.name, originalItem.categories.firstOrNull()?.id)

                    ItemForm(
                        categoryOptions,
                        "Edit Item",
                        "Save",
                        itemBody,
                        onCancel = viewModel::cancelItemOperation,
                    ) { body ->
                        coroutineScope.launch {
                            viewModel.updateItem(originalItem.id, body)
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
