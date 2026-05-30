package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.util.formatCents
import com.julianfortune.glacier.view.Item
import com.julianfortune.glacier.view.ScrollableColumn
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import com.julianfortune.glacier.viewModel.data.DeliveryAction
import com.julianfortune.glacier.viewModel.data.DeliveryEntryAction
import kotlinx.coroutines.launch
import java.time.format.FormatStyle

fun calculateEntryTotalCostCents(entry: Entry): Long {
    if (entry.costStatus == CostStatus.NO_COST) {
        return 0L
    }

    return entry.unitCount * entry.unitCostCents
}

fun calculateDeliveryTotalCostCents(delivery: DeliveryDetail): Long {
    val totalUnitsCost = delivery.entries?.map { calculateEntryTotalCostCents(it) }?.reduceOrNull { a, b -> a + b } ?: 0

    return totalUnitsCost + (delivery.feesCents ?: 0) + (delivery.taxesCents ?: 0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesListDetailView(viewModel: DeliveryViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val deliveryHeadlines by viewModel.allDeliveries.collectAsState(emptyList())
    val selectedDeliveryId by viewModel.selectedDeliveryId.collectAsState()

    val itemMap by viewModel.itemMap.collectAsState()
    val supplierMap by viewModel.supplierMap.collectAsState()

    val deliveryItems = remember(deliveryHeadlines, selectedDeliveryId) {
        deliveryHeadlines.map { delivery ->
            val dateString = formatLocalDate(delivery.data.receivedDate)
            val isSelected = delivery.id == selectedDeliveryId

            Item(
                dateString,
                { viewModel.selectDelivery(delivery.id) },
                isSelected,
            )
        }
    }

    val deliveryDetail by viewModel.selectedDeliveryDetail.collectAsState()
    val deliveryAction by viewModel.deliveryAction
    val deliveryEntryAction by viewModel.deliveryEntryAction

    if (deliveryAction != null) {
        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                when (deliveryAction) {
                    is DeliveryAction.CreateNew -> {
                        NewDeliveryForm(
                            viewModel,
                            "New Delivery",
                            "Create",
                            onSubmit = { newDelivery ->
                                coroutineScope.launch {
                                    val delivery = DeliveryDetail(
                                        newDelivery.receivedDate,
                                        newDelivery.supplierId,
                                        newDelivery.taxesCents,
                                        newDelivery.feesCents,
                                        emptyList()
                                    )
                                    val newDeliveryId = viewModel.saveDelivery(delivery)
                                    viewModel.newDeliveryCreated(newDeliveryId)
                                }
                            })
                    }

                    is DeliveryAction.Edit -> {
                        val delivery = (deliveryAction as DeliveryAction.Edit).delivery
                        val headline = DeliveryHeadline(
                            delivery.data.receivedDate,
                            delivery.data.supplierId,
                            delivery.data.taxesCents,
                            delivery.data.feesCents,
                        )
                        NewDeliveryForm(
                            viewModel,
                            "Edit Delivery",
                            "Save",
                            initialDelivery = headline,
                            onSubmit = { updated ->
                                coroutineScope.launch {
                                    val delivery = Entity(
                                        delivery.id, DeliveryDetail(
                                            updated.receivedDate,
                                            updated.supplierId,
                                            updated.taxesCents,
                                            updated.feesCents,
                                            delivery.data.entries
                                        )
                                    )
                                    viewModel.updateDelivery(delivery)
                                    viewModel.newDeliveryCreated(delivery.id)
                                }
                            }
                        )
                    }

                    else -> throw Error("`deliveryAction` must not be `null`")
                }
            }
        }
    }

    if (deliveryEntryAction != null) {
        if (selectedDeliveryId == null) {
            throw IllegalStateException("Unable to create an entry without a delivery selected")
        }

        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                when (deliveryEntryAction) {
                    is DeliveryEntryAction.CreateNew -> {
                        NewEntryForm(
                            viewModel,
                            "New Entry",
                            "Create",
                            onSubmit = { entry ->
                                coroutineScope.launch {
                                    viewModel.saveEntry(selectedDeliveryId!!, entry)
                                    viewModel.dismissEntryModal()
                                }
                            }
                        )
                    }

                    is DeliveryEntryAction.Edit -> {
                        NewEntryForm(
                            viewModel,
                            "Edit Entry",
                            "Update",
                            initialEntry = (deliveryEntryAction as DeliveryEntryAction.Edit).entry,
                            onSubmit = { newEntry ->
                                val currentDeliveryDetail: Entity<DeliveryDetail> =
                                    deliveryDetail
                                        ?: throw AssertionError("Unable to delete an entry because `deliveryDetail` is unexpectedly null")

                                val updatedDelivery = currentDeliveryDetail.copy(
                                    data = currentDeliveryDetail.data.copy(
                                        entries = currentDeliveryDetail.data.entries?.mapIndexed { i, e ->
                                            when {
                                                i == (deliveryEntryAction as DeliveryEntryAction.Edit).index -> newEntry
                                                else -> e
                                            }
                                        }
                                    )
                                )

                                coroutineScope.launch {
                                    viewModel.updateDelivery(updatedDelivery)
                                    viewModel.dismissEntryModal()
                                }
                            }
                        )
                    }

                    else -> throw Error("`deliveryEntryAction` must not be `null`")
                }
            }
        }
    }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            // TODO(P1): Refactor this header / list layout to look like Categories page (see screenshot mockup)
            //  into a reusable component (settings for: elevation (?) to set bg color, handler for `createNew`, (later) search, filter, etc.)
            // TODO(P1): Add support for dropdown ellipses menu with support for delete and edit (configurable)
            Column(modifier = Modifier.width(240.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(8.dp),
                ) {
                    FilledTonalButton(
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.height(32.dp).fillMaxWidth().pointerHoverIcon(PointerIcon.Hand),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        onClick = {
                            viewModel.showNewDelivery()
                        },
                    ) {
                        Text("New")
                    }
                }
                ScrollableColumn(deliveryItems)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            if (deliveryDetail == null) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No delivery selected.")
                }
            } else {
                // TODO(P2): Move into separate composable file
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column() {
                                Spacer(Modifier.height(4.dp)) // Better align with icon buttons
                                Text(
                                    deliveryDetail?.data?.receivedDate?.let { formatLocalDate(it, FormatStyle.FULL) }
                                        .toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                )

                                Text("${supplierMap[deliveryDetail?.data?.supplierId]?.data?.name}")
                                Text("Fees: $${formatCents(deliveryDetail?.data?.feesCents ?: 0)}")
                                Text("Taxes: $${formatCents(deliveryDetail?.data?.taxesCents ?: 0)}")
                            }
                            Row {
                                IconButton(
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                    onClick = {
                                        // TODO: Better error-handling
                                        viewModel.showEditDelivery(deliveryDetail!!)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "Edit delivery details"
                                    )
                                }
                                IconButton(
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                    onClick = {
                                        // TODO: #14
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete delivery"
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Entries",
                            style = MaterialTheme.typography.headlineSmall,
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(vertical = 24.dp)
                        ) {
                            // Header row
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(horizontal = 16.dp).padding(vertical = 8.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        text = "Item",
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    // TODO(P2): (?) Add `Unit`

                                    Text(
                                        text = "Unit Cost",
                                        modifier = Modifier.width(80.dp),
                                        textAlign = TextAlign.End
                                    )

                                    Text(
                                        text = "Count",
                                        modifier = Modifier.width(60.dp),
                                        textAlign = TextAlign.End
                                    )

                                    Text(
                                        text = "Weight (lbs)",
                                        modifier = Modifier.width(104.dp),
                                        textAlign = TextAlign.End
                                    )

                                    Text(
                                        text = "Total",
                                        modifier = Modifier.width(80.dp),
                                        textAlign = TextAlign.End
                                    )

                                    Row(modifier = Modifier.width(32.dp)) {}
                                }
                            }

                            val entries = deliveryDetail?.data?.entries
                            when {
                                entries == null || entries.isEmpty() -> Row(
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Text("None", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }

                                else -> {
                                    entries.mapIndexed { index, entry ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                                        ) {
                                            Text(
                                                text = itemMap[entry.itemId]?.data?.name ?: "...",
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Text(
                                                text = "$${formatCents(entry.unitCostCents)}",
                                                modifier = Modifier.width(80.dp),
                                                textAlign = TextAlign.End,
                                                fontFamily = FontFamily.Monospace
                                            )

                                            Text(
                                                text = "${entry.unitCount}",
                                                modifier = Modifier.width(60.dp),
                                                textAlign = TextAlign.End,
                                                fontFamily = FontFamily.Monospace
                                            )

                                            val totalWeightInPounds = entry.unitWeight.times(entry.unitCount).toPounds()
                                            Text(
                                                text = "%.2f".format(totalWeightInPounds),
                                                modifier = Modifier.width(104.dp),
                                                textAlign = TextAlign.End,
                                                fontFamily = FontFamily.Monospace
                                            )

                                            val totalEntryCostCents = calculateEntryTotalCostCents(entry)
                                            Text(
                                                text = "$${formatCents(totalEntryCostCents)}",
                                                modifier = Modifier.width(80.dp),
                                                textAlign = TextAlign.End,
                                                fontFamily = FontFamily.Monospace
                                            )

                                            Row(
                                                modifier = Modifier.width(32.dp)
                                            ) {
                                                EntryOptionsDropdownMenu(
                                                    edit = {
                                                        viewModel.showEditEntry(index, entry)
                                                    },
                                                    delete = {
                                                        // TODO(P4): Revisit this code to try to make more fluent
                                                        val currentDeliveryDetail: Entity<DeliveryDetail> =
                                                            deliveryDetail
                                                                ?: throw AssertionError("Unable to delete an entry because `deliveryDetail` is unexpectedly null")

                                                        val updatedDelivery = currentDeliveryDetail.copy(
                                                            data = currentDeliveryDetail.data.copy(
                                                                entries = currentDeliveryDetail.data.entries?.filterIndexed { i, _ -> i != index }
                                                            )
                                                        )

                                                        coroutineScope.launch {
                                                            viewModel.updateDelivery(updatedDelivery)
                                                        }
                                                    },
                                                )
                                            }
                                        }

                                        // TODO(P2): Purchasing Account & Program allocations
                                    }
                                }
                            }
                        }



                        Row {
                            Button(
                                onClick = { viewModel.showNewEntry() },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Text("New Entry")
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Total cost of delivery
                        val totalCostCents = deliveryDetail?.let { calculateDeliveryTotalCostCents(it.data) }
                        totalCostCents?.let {
                            Text(
                                text = "$${formatCents(it)}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

        }
    }
}