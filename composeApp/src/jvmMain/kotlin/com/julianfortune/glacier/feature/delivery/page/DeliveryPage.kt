package com.julianfortune.glacier.feature.delivery.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.core.util.formatCents
import com.julianfortune.glacier.data.domain.*
import com.julianfortune.glacier.feature.delivery.page.*
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryEntryAction
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryPageDetailsState
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryPageEntryState
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryPageState
import com.julianfortune.glacier.feature.delivery.page.data.DeliveryPageSummaryState
import com.julianfortune.glacier.feature.delivery.page.data.EntryRowState
import com.julianfortune.glacier.feature.delivery.page.ui.DeliveryPageContent
import com.julianfortune.glacier.feature.delivery.page.ui.DeliveryTopBar
import com.julianfortune.glacier.feature.delivery.page.ui.EditDelivery
import com.julianfortune.glacier.feature.delivery.page.ui.NewEntryForm
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
fun DeliveryPage(
    selectedId: Long,
    viewModel: DeliveryPageViewModel = koinViewModel(),
) {
    LaunchedEffect(selectedId) {
        viewModel.setCurrentId(selectedId)
    }

    // TODO: Use an ADT to represent the data better e.g., DeliveryUiState := Loading, Error(...), Delivery(data)
    val deliveryDetail by viewModel.delivery.collectAsState()
    val deliveryEntryAction by viewModel.deliveryEntryAction

    var editDetailsDialogIsOpen by remember { mutableStateOf(false) }
    var deleteDialogIsOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        deliveryDetail?.let { delivery ->
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
            val pageState = DeliveryPageState(
                DeliveryPageDetailsState(
                    formatLocalDate(delivery.received, FormatStyle.MEDIUM),
                    supplierName ?: "",
                    fees,
                    taxes,
                ),
                DeliveryPageEntryState(
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
                    total,
                ),
                DeliveryPageSummaryState(
                    subtotal,
                    fees,
                    taxes,
                    total,
                )
            )

            DeliveryPageContent(
                pageState,
                onClickEditDetails = {
                    editDetailsDialogIsOpen = true
                },
                onClickAddEntry = {
                    viewModel.showNewEntry()
                },
                onClickEditEntry = {
                    // TODO ...
                },
                onClickDeleteEntry = {
                    // TODO ...
                },
            )
        }
    }

    if (editDetailsDialogIsOpen) {
        Dialog(
            onDismissRequest = { editDetailsDialogIsOpen = false },
        ) {
            EditDelivery(
                delivery = deliveryDetail ?: throw NoSuchElementException("`deliveryDetail` must be defined"),
                onCancel = {
                    editDetailsDialogIsOpen = false
                },
                onSuccess = {
                    editDetailsDialogIsOpen = false
                }
            )
        }
    }

    if (deleteDialogIsOpen) {
        Dialog(
            onDismissRequest = { deleteDialogIsOpen = false },
        ) {
            // TODO(?): Move into own component like EditDelivery
            ConfirmDeleteEntityForm(
                deliveryDetail!!.id,
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
                    is DeliveryEntryAction.CreateNew -> {
                        NewEntryForm(
                            "New Entry",
                            "Create",
                            viewModel.allItems.collectAsState(initial = emptyList()).value,
                            onCancel = {
                                dismissSheet()
                            },
                            onSubmit = { entry ->
                                viewModel.saveEntry(deliveryDetail!!.id, entry)
                                dismissSheet()
                            }
                        )
                    }

//                    is DeliveryEntryAction.Edit -> {
//                        NewEntryForm(
//                            "Edit Entry",
//                            "Update",
//                            viewModel.allItems.collectAsState(initial = emptyList()).value,
//                            initialEntry = (deliveryEntryAction as DeliveryEntryAction.Edit).entry,
//                            onCancel = {
//                                dismissSheet()
//                            },
//                            onSubmit = { newEntry ->
//                                val currentDeliveryDetail: Entity<DeliveryDetail> =
//                                    deliveryDetail
//                                        ?: throw AssertionError("Unable to delete an entry because `deliveryDetail` is unexpectedly null")
//
//                                val updatedDelivery = currentDeliveryDetail.copy(
//                                    data = currentDeliveryDetail.copy(
//                                        entries = currentDeliveryDetail.entries?.mapIndexed { i, e ->
//                                            when {
//                                                i == (deliveryEntryAction as DeliveryEntryAction.Edit).index -> newEntry
//                                                else -> e
//                                            }
//                                        }
//                                    )
//                                )
//
//                                coroutineScope.launch {
//                                    viewModel.updateDelivery(updatedDelivery)
//                                    dismissSheet()
//                                }
//                            }
//                        )
//                    }

                    else -> throw Error("`deliveryEntryAction` must not be `null`")
                }
            }
        }
    }
}


