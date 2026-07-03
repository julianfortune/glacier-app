package com.julianfortune.glacier.feature.namedentity

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
import com.julianfortune.glacier.data.common.NamedEntity
import com.julianfortune.glacier.ui.common.CollectionView
import com.julianfortune.glacier.ui.common.ConfirmDeleteEntityForm
import com.julianfortune.glacier.ui.common.EntityOptionsDropdownMenu
import com.julianfortune.glacier.feature.namedentity.data.EntityOperation
import com.julianfortune.glacier.feature.namedentity.ui.UpdateNamedEntityForm
import kotlinx.coroutines.launch


// TODO: Proactively check if entity is referenced by > 1 other entities and can't be deleted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : NamedEntity> NamedEntityListView(
    viewModel: NamedEntityViewModel<T>,
    title: String,
    entityName: String
) {
    val coroutineScope = rememberCoroutineScope()

    val entities by viewModel.allEntities.collectAsState(emptyList())
    val currentOperation by viewModel.operation

    Column {
        CollectionView(
            title,
            entities,
            null,
            onClickCreateNew = {
                viewModel.showCreateNew()
            }
        ) { entity, modifier, elevation ->
            ListItem(
                headlineContent = {
                    Text(entity.name)
                },
                modifier = modifier.clickable(
                    enabled = true,
                    onClick = {
                        viewModel.showEdit(entity)
                    }
                ),
                tonalElevation = elevation,
                trailingContent = {
                    EntityOptionsDropdownMenu(
                        edit = {
                            viewModel.showEdit(entity)
                        },
                        delete = {
                            viewModel.showDelete(entity)
                        },
                    )
                }
            )
        }
    }

    // Modal
    if (currentOperation != null) {
        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                when (currentOperation) {
                    is EntityOperation.CreateNew -> {
                        UpdateNamedEntityForm(viewModel, "Create $entityName", "Create") { newName ->
                            coroutineScope.launch {
                                viewModel.save(newName)
                                viewModel.dismissOperation()
                            }
                        }
                    }

                    is EntityOperation.Edit -> {
                        val original = (currentOperation as EntityOperation.Edit).entity

                        UpdateNamedEntityForm(
                            viewModel,
                            "Edit $entityName",
                            "Save",
                            original
                        ) { newName ->
                            coroutineScope.launch {
                                viewModel.update(original.id, newName)
                                viewModel.dismissOperation()
                            }
                        }
                    }

                    is EntityOperation.Delete -> {
                        val id = (currentOperation as EntityOperation.Delete).id

                        ConfirmDeleteEntityForm(
                            id,
                            "Delete $entityName",
                            onCancel = {
                                viewModel.dismissOperation()
                            },
                            onConfirm = {
                                coroutineScope.launch {
                                    viewModel.delete(id)
                                    viewModel.dismissOperation()
                                }
                            }
                        )
                    }

                    else -> throw Error("`operation` must not be `null`")
                }
            }
        }
    }
}
