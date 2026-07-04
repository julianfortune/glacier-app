package com.julianfortune.glacier.feature.delivery.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.core.util.formatCents
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.feature.delivery.common.data.DeliveryBody
import com.julianfortune.glacier.feature.delivery.common.ui.DeliveryForm
import com.julianfortune.glacier.feature.delivery.detail.data.*
import com.julianfortune.glacier.feature.delivery.detail.ui.DeliveryPageContent
import com.julianfortune.glacier.feature.delivery.detail.ui.DeliveryTopBar
import com.julianfortune.glacier.feature.delivery.detail.ui.NewEntryForm
import com.julianfortune.glacier.ui.common.ConfirmDeleteEntityForm
import com.julianfortune.glacier.ui.common.Dialog
import com.julianfortune.glacier.ui.common.SideSheet
import com.julianfortune.glacier.ui.common.formatLocalDate
import org.koin.compose.viewmodel.koinViewModel
import java.time.format.FormatStyle


fun calculateEntryTotalCostCents(entry: Delivery.Entry): Long {
    if (entry.costStatus == CostStatus.NO_COST) {
        return 0L
    }

    return entry.unitCount * entry.unitCostCents
}

fun calculateDeliverySubTotalCostCents(delivery: Delivery): Long {
    val totalUnitsCost = delivery.entries?.map { calculateEntryTotalCostCents(it) }?.reduceOrNull { a, b -> a + b } ?: 0

    return totalUnitsCost
}

fun calculateDeliveryTotalCostCents(delivery: Delivery): Long {
    val totalUnitsCost = delivery.entries?.map { calculateEntryTotalCostCents(it) }?.reduceOrNull { a, b -> a + b } ?: 0

    return totalUnitsCost + (delivery.feesCents ?: 0) + (delivery.taxesCents ?: 0)
}

fun calculateEntryTotalWeight(entry: Delivery.Entry): Weight {
    return entry.unitWeight.times(entry.unitCount)
}

fun calculateDeliveryTotalWeightPounds(delivery: Delivery): Double {
    return (delivery.entries).fold(0.0) { sum, entry ->
        sum + calculateEntryTotalWeight(entry).toPounds()
    }
}

@Composable
fun DeliveryDetail(
    selectedId: Long,
    viewModel: DeliveryDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(selectedId) {
        viewModel.setCurrentId(selectedId)
    }

    // TODO: Use an ADT to represent the data better e.g., DeliveryUiState := Loading, Error(...), Delivery(data)
    val delivery by viewModel.delivery.collectAsState()
    val deliveryEntryAction by viewModel.entryAction

    var editDetailsDialogIsOpen by remember { mutableStateOf(false) }
    var deleteDialogIsOpen by remember { mutableStateOf(false) }

    val supplierOptions by viewModel.supplierOptions.collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        delivery?.let { delivery ->
            // TODO: This should live in ViewModel or some other layer for business logic
            val supplierName = delivery.supplier?.name

            var pageTitle = "Delivery " + formatLocalDate(delivery.received, FormatStyle.MEDIUM)
            supplierName?.let { pageTitle += " • ${it}" }

            DeliveryTopBar(
                pageTitle,
                onClickDelete = {
                    deleteDialogIsOpen = true
                }
            )

            // TODO: This should live in ViewModel or some other layer for business logic
            val totalCount = (delivery.entries?.sumOf { it.unitCount } ?: 0).toString()
            val totalWeight = calculateDeliveryTotalWeightPounds(delivery).toString()
            val subtotal = "$" + formatCents(calculateDeliverySubTotalCostCents(delivery))
            val fees = "$" + formatCents(delivery.feesCents ?: 0)
            val taxes = "$" + formatCents(delivery.taxesCents ?: 0)
            val total = "$" + formatCents(calculateDeliveryTotalCostCents(delivery))
            val pageState = DeliveryContentState(
                formatLocalDate(delivery.received, FormatStyle.MEDIUM),
                supplierName ?: "",
                delivery.entries.map { e ->
                    // TODO: Need to fetch all the data from the DB atomically
                    val totalWeight = calculateEntryTotalWeight(e)
                    val totalCostCents = "$" + formatCents(calculateEntryTotalCostCents(e))
                    EntryRowState(
                        e.item.name,
                        null,
                        null,
                        e.unitCount.toString(),
                        totalWeight.toPounds().toString(),
                        totalCostCents,
                    )
                },
                totalCount,
                totalWeight,
                subtotal,
                fees,
                taxes,
                total,
            )

            DeliveryPageContent(
                pageState,
                onClickEditDetails = {
                    editDetailsDialogIsOpen = true
                },
                onClickAddEntry = {
                    viewModel.showNewEntry()
                },
                onClickEditEntry = { entry ->
                    viewModel.showEditEntry(entry)
                },
                onClickDeleteEntry = { entryId ->
                    TODO()
                },
            )
        }
    }

    if (editDetailsDialogIsOpen) {
        Dialog(
            onDismissRequest = { editDetailsDialogIsOpen = false },
        ) {
            DeliveryForm(
                title = "Edit Delivery",
                supplierOptions = supplierOptions,
                // TODO: Type-safe state management
                initialDelivery = delivery?.let {
                    DeliveryBody(
                        it.received,
                        it.supplier.id,
                        it.taxesCents,
                        it.feesCents
                    )
                },
                onCancel = {
                    editDetailsDialogIsOpen = false
                },
                onSubmit = { newDelivery ->
                    // TODO: Type-safe state management
                    viewModel.updateDelivery(delivery!!.id, newDelivery)

                    // TODO: Automatically update selected ID to the newly created delivery
                    editDetailsDialogIsOpen = false
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if (deleteDialogIsOpen) {
        Dialog(
            onDismissRequest = { deleteDialogIsOpen = false },
        ) {
            // TODO(?): Move into own component like EditDelivery
            ConfirmDeleteEntityForm(
                delivery!!.id,
                "Delete Delivery",
                onCancel = {
                    deleteDialogIsOpen = false
                },
                onConfirm = { id ->
                    deleteDialogIsOpen = false
                    viewModel.deleteDelivery(id)
                }
            )
        }
    }

    if (deliveryEntryAction != null) {
        SideSheet(
            onClose = {
                viewModel.cancelEntryOperation()
            },
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) { dismissSheet ->
            Column(
                modifier = Modifier
                    .width(640.dp)
                    .padding(16.dp),
            ) {
                when (deliveryEntryAction) {
                    is EntryAction.CreateNew -> {
                        NewEntryForm(
                            "New Entry",
                            "Create",
                            viewModel.allItems.collectAsState(initial = emptyList()).value,
                            onCancel = {
                                dismissSheet()
                            },
                            onSubmit = { entry ->
                                viewModel.saveEntry(delivery!!.id, entry)
                                dismissSheet()
                            }
                        )
                    }

                    is EntryAction.Edit -> {
                        NewEntryForm(
                            "Edit Entry",
                            "Update",
                            viewModel.allItems.collectAsState(initial = emptyList()).value,
                            initialEntry = (deliveryEntryAction as EntryAction.Edit).entry,
                            onCancel = {
                                dismissSheet()
                            },
                            onSubmit = { newEntry ->
                                val id = (deliveryEntryAction as EntryAction.Edit).entry.id

                                viewModel.updateEntry(id, newEntry)
                                dismissSheet()
                            }
                        )
                    }

                    else -> throw Error("`deliveryEntryAction` must not be `null`")
                }
            }
        }
    }
}


