package com.julianfortune.glacier.feature.delivery.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.feature.delivery.common.ui.DeliveryForm
import com.julianfortune.glacier.feature.delivery.common.ui.EntryForm
import com.julianfortune.glacier.feature.delivery.detail.data.DeliveryAction
import com.julianfortune.glacier.feature.delivery.detail.data.DeliveryDetailState
import com.julianfortune.glacier.feature.delivery.detail.data.EntryAction
import com.julianfortune.glacier.feature.delivery.detail.ui.DeliveryPageContent
import com.julianfortune.glacier.feature.delivery.detail.ui.DeliveryTopBar
import com.julianfortune.glacier.feature.delivery.detail.ui.EntrySideSheet
import com.julianfortune.glacier.ui.common.ConfirmDeleteEntityForm
import com.julianfortune.glacier.ui.common.Dialog
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun DeliveryDetail(
    selectedId: Long,
    viewModel: DeliveryDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(selectedId) {
        viewModel.setCurrentId(selectedId)
    }

    val state by viewModel.uiState.collectAsState()

    // TODO(!!): Turn these into rich state objects with error information
    val deliveryAction by viewModel.deliveryAction
    val entryAction by viewModel.entryAction

    val supplierOptions by viewModel.supplierOptions.collectAsState(emptyList())
    val itemOptions by viewModel.itemOptions.collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            DeliveryDetailState.Failure -> Text("Undefined")
            DeliveryDetailState.Loading -> Text("Loading...")
            is DeliveryDetailState.Success -> {
                DeliveryTopBar(
                    (state as DeliveryDetailState.Success).title,
                    onClickDelete = {
                        viewModel.showDeleteDelivery(selectedId)
                    }
                )

                DeliveryPageContent(
                    (state as DeliveryDetailState.Success).content,
                    onClickEditDetails = {
                        viewModel.showEditDelivery(selectedId)
                    },
                    onClickAddEntry = viewModel::showNewEntry,
                    onClickEditEntry = { entryId ->
                        viewModel.showEditEntry(entryId)
                    },
                    onClickDeleteEntry = { entryId ->
                        viewModel.showDeleteEntry(entryId)
                    },
                )
            }
        }
    }

    deliveryAction?.let { action ->
        when (action) {
            is DeliveryAction.Edit -> {
                Dialog(
                    onDismissRequest = { viewModel.cancelDeliveryOperation() },
                ) {
                    DeliveryForm(
                        title = "Edit Delivery",
                        supplierOptions = supplierOptions,
                        initialDelivery = action.delivery,
                        onCancel = {
                            viewModel.cancelDeliveryOperation()
                        },
                        onSubmit = { newDelivery ->
                            viewModel.updateDelivery(action.id, newDelivery)
                            viewModel.cancelDeliveryOperation()
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is DeliveryAction.Delete -> {
                Dialog(
                    onDismissRequest = { viewModel.cancelDeliveryOperation() },
                ) {
                    ConfirmDeleteEntityForm(
                        selectedId,
                        "Delete Delivery",
                        onCancel = {
                            viewModel.cancelDeliveryOperation()
                        },
                        onConfirm = { id ->
                            viewModel.deleteDelivery(id)
                            viewModel.cancelDeliveryOperation()
                        }
                    )
                }
            }
        }
    }

    entryAction?.let { action ->
        when (action) {
            is EntryAction.CreateNew -> {
                EntrySideSheet(
                    onClose = { viewModel.cancelEntryOperation() }
                ) { dismissSheet ->
                    EntryForm(
                        "New Entry",
                        "Create",
                        itemOptions,
                        onCancel = {
                            dismissSheet()
                        },
                        onSubmit = { entry ->
                            // TODO(P2): Safer way to get the deliveryId from some sort of state ADT
                            viewModel.saveEntry(selectedId, entry)
                            dismissSheet()
                        }
                    )
                }
            }

            is EntryAction.Edit -> {
                EntrySideSheet(
                    onClose = { viewModel.cancelEntryOperation() }
                ) { dismissSheet ->
                    EntryForm(
                        "Edit Entry",
                        "Update",
                        itemOptions,
                        initialEntry = action.entry,
                        onCancel = {
                            dismissSheet()
                        },
                        onSubmit = { newEntry ->
                            viewModel.updateEntry(action.id, newEntry)
                            dismissSheet()
                        }
                    )
                }
            }

            is EntryAction.Delete -> {
                Dialog(
                    onDismissRequest = { viewModel.cancelEntryOperation() },
                ) {
                    ConfirmDeleteEntityForm(
                        action.id,
                        "Delete Entry",
                        onCancel = {
                            viewModel.cancelEntryOperation()
                        },
                        onConfirm = { id ->
                            viewModel.deleteEntryById(id)
                            viewModel.cancelEntryOperation()
                        }
                    )
                }
            }
        }
    }
}



