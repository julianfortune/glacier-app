package com.julianfortune.glacier.view.supplier

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
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.view.shared.CollectionView
import com.julianfortune.glacier.view.shared.ConfirmDeleteEntityForm
import com.julianfortune.glacier.view.shared.EntityOptionsDropdownMenu
import com.julianfortune.glacier.viewModel.SupplierViewModel
import com.julianfortune.glacier.viewModel.data.EntityOperation
import kotlinx.coroutines.launch


// TODO: Proactively check if `Supplier` is referenced by any Deliveries and prevent deleting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierListView(viewModel: SupplierViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val suppliers by viewModel.suppliers.collectAsState(emptyList())
    val supplierOperation by viewModel.supplierOperation

    Column {
        CollectionView(
            "Suppliers",
            suppliers,
            null,
            onClickCreateNew = {
                viewModel.showNewSupplier()
            }
        ) { supplier, modifier, elevation ->
            ListItem(
                headlineContent = {
                    Text(supplier.data.name)
                },
                modifier = modifier.clickable(
                    enabled = true,
                    onClick = {
                        viewModel.showEditSupplier(supplier)
                    }
                ),
                tonalElevation = elevation,
                trailingContent = {
                    EntityOptionsDropdownMenu(
                        edit = {
                            viewModel.showEditSupplier(supplier)
                        },
                        delete = {
                            viewModel.showDeleteSupplier(supplier)
                        },
                    )
                }
            )
        }
    }

    // Modal
    if (supplierOperation != null) {
        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                when (supplierOperation) {
                    is EntityOperation.CreateNew -> {
                        SupplierForm(viewModel, "Create Supplier", "Create") { supplier ->
                            coroutineScope.launch {
                                viewModel.saveSupplier(supplier)
                                viewModel.dismissSupplierModal()
                            }
                        }
                    }

                    is EntityOperation.Edit -> {
                        val originalSupplier = (supplierOperation as EntityOperation.Edit).entity

                        SupplierForm(viewModel, "Edit Supplier", "Save", originalSupplier.data) { supplier ->
                            coroutineScope.launch {
                                viewModel.updateSupplier(Entity(originalSupplier.id, supplier))
                                viewModel.dismissSupplierModal()
                            }
                        }
                    }

                    is EntityOperation.Delete -> {
                        val supplierId = (supplierOperation as EntityOperation.Delete).id
                        ConfirmDeleteEntityForm(
                            supplierId,
                            "Delete Supplier",
                            onCancel = {
                                viewModel.cancelSupplierOperation()
                            },
                            onConfirm = {
                                coroutineScope.launch {
                                    viewModel.deleteSupplier(supplierId)
                                    viewModel.dismissSupplierModal()
                                }
                            }
                        )
                    }

                    else -> throw Error("`supplierOperation` must not be `null`")
                }
            }
        }
    }
}
