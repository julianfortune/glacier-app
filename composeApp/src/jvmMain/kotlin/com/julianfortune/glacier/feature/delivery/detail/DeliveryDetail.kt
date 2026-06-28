package com.julianfortune.glacier.feature.delivery.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.core.util.formatCents
import com.julianfortune.glacier.ui.common.formatLocalDate
import java.time.format.FormatStyle
import kotlin.collections.get


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

fun calculateEntryTotalWeight(entry: Entry): Weight {
    return entry.unitWeight.times(entry.unitCount)
}

fun calculateDeliveryTotalWeightPounds(delivery: DeliveryDetail): Double {
    return (delivery.entries ?: emptyList()).fold(0.0) { sum, entry ->
        sum + calculateEntryTotalWeight(entry).toPounds()
    }
}

@Composable
fun DeliveryDetail(viewModel: DeliveryDetailViewModel) {

    // TODO: Use an ADT to represent the data better e.g., DeliveryUiState := Loading, Error(...), Delivery(data)
    val deliveryDetail by viewModel.deliveryDetail.collectAsState()

    val itemMap by viewModel.itemMap.collectAsState()
    val supplierMap by viewModel.supplierMap.collectAsState()

//    if (deliveryEntryAction != null) {
//        SideSheet(
//            onClose = {
//                viewModel.dismissEntryModal()
//            },
//            color = MaterialTheme.colorScheme.surface,
//            tonalElevation = 1.dp,
//        ) { dismissSheet ->
//            Column(
//                modifier = Modifier
//                    .width(640.dp)
//                    .padding(16.dp),
//            ) {
//                when (deliveryEntryAction) {
//                    is DeliveryEntryAction.CreateNew -> {
//                        NewEntryForm(
//                            viewModel,
//                            "New Entry",
//                            "Create",
//                            onCancel = {
//                                dismissSheet()
//                            },
//                            onSubmit = { entry ->
//                                coroutineScope.launch {
//                                    viewModel.saveEntry(selectedDeliveryId!!, entry)
//                                    dismissSheet()
//                                }
//                            }
//                        )
//                    }
//
//                    is DeliveryEntryAction.Edit -> {
//                        NewEntryForm(
//                            viewModel,
//                            "Edit Entry",
//                            "Update",
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
//                                    data = currentDeliveryDetail.data.copy(
//                                        entries = currentDeliveryDetail.data.entries?.mapIndexed { i, e ->
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
//
//                    else -> throw Error("`deliveryEntryAction` must not be `null`")
//                }
//            }
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            deliveryDetail?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(Modifier.height(4.dp)) // Better align with icon buttons
                        Text(
                            formatLocalDate(it.data.receivedDate, FormatStyle.FULL),
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        Text("${supplierMap[it.data.supplierId]?.data?.name}")
                        Text("Fees: $${formatCents(it.data.feesCents ?: 0)}")
                        Text("Taxes: $${formatCents(it.data.taxesCents ?: 0)}")
                    }
//                Row {
//                    IconButton(
//                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
//                        onClick = {
//                            viewModel.showEditDelivery(deliveryDetail)
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Outlined.Edit,
//                            contentDescription = "Edit delivery details"
//                        )
//                    }
//                    IconButton(
//                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
//                        onClick = {
//                            viewModel.showDeleteDelivery(deliveryDetail)
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Outlined.Delete,
//                            contentDescription = "Delete delivery"
//                        )
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(16.dp))
//
//            Text(
//                "Entries",
//                style = MaterialTheme.typography.headlineSmall,
//            )
//
//            Column(
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                modifier = Modifier.padding(vertical = 24.dp)
//            ) {
//                // Header row
//                Surface(
//                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
//                    modifier = Modifier
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                        modifier = Modifier.padding(horizontal = 16.dp).padding(vertical = 8.dp)
//                            .fillMaxWidth(),
//                    ) {
//                        Text(
//                            text = "Item",
//                            modifier = Modifier.weight(1f),
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )
//
//                        // TODO(P2): (?) Add `Unit`
//
//                        Text(
//                            text = "Unit Cost",
//                            modifier = Modifier.width(80.dp),
//                            textAlign = TextAlign.End
//                        )
//
//                        Text(
//                            text = "Count",
//                            modifier = Modifier.width(60.dp),
//                            textAlign = TextAlign.End
//                        )
//
//                        Text(
//                            text = "Weight (lbs)",
//                            modifier = Modifier.width(104.dp),
//                            textAlign = TextAlign.End
//                        )
//
//                        Text(
//                            text = "Total",
//                            modifier = Modifier.width(80.dp),
//                            textAlign = TextAlign.End
//                        )
//
//                        Row(modifier = Modifier.width(32.dp)) {}
//                    }
//                }
//
//                val entries = deliveryDetail.data.entries
//                when {
//                    entries == null || entries.isEmpty() -> Row(
//                        modifier = Modifier.padding(horizontal = 16.dp)
//                    ) {
//                        Text("None", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//                    }
//
//                    else -> {
//                        entries.mapIndexed { index, entry ->
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
//                            ) {
//                                SelectionContainer {
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
//                                    ) {
//                                        Text(
//                                            text = itemMap[entry.itemId]?.data?.name ?: "...",
//                                            modifier = Modifier.weight(1f),
//                                            maxLines = 1,
//                                            overflow = TextOverflow.Ellipsis
//                                        )
//
//                                        Text(
//                                            text = "$${formatCents(entry.unitCostCents)}",
//                                            modifier = Modifier.width(80.dp),
//                                            textAlign = TextAlign.End,
//                                            fontFamily = FontFamily.Monospace
//                                        )
//
//                                        Text(
//                                            text = "${entry.unitCount}",
//                                            modifier = Modifier.width(60.dp),
//                                            textAlign = TextAlign.End,
//                                            fontFamily = FontFamily.Monospace
//                                        )
//
//                                        val totalWeightInPounds = entry.unitWeight.times(entry.unitCount).toPounds()
//                                        Text(
//                                            text = "%.2f".format(totalWeightInPounds),
//                                            modifier = Modifier.width(104.dp),
//                                            textAlign = TextAlign.End,
//                                            fontFamily = FontFamily.Monospace
//                                        )
//
//                                        val totalEntryCostCents = calculateEntryTotalCostCents(entry)
//                                        Text(
//                                            text = "$${formatCents(totalEntryCostCents)}",
//                                            modifier = Modifier.width(80.dp),
//                                            textAlign = TextAlign.End,
//                                            fontFamily = FontFamily.Monospace
//                                        )
//
//                                        Row(
//                                            modifier = Modifier.width(32.dp)
//                                        ) {
//                                            EntityOptionsDropdownMenu(
//                                                edit = {
//                                                    viewModel.showEditEntry(index, entry)
//                                                },
//                                                delete = {
//                                                    // TODO(P4): Revisit this code to try to make more fluent
//                                                    val currentDeliveryDetail: Entity<DeliveryDetail> = deliveryDetail
//
//                                                    val updatedDelivery = currentDeliveryDetail.copy(
//                                                        data = currentDeliveryDetail.data.copy(
//                                                            entries = currentDeliveryDetail.data.entries?.filterIndexed { i, _ -> i != index }
//                                                        )
//                                                    )
//
//                                                    viewModel.updateDelivery(updatedDelivery)
//                                                },
//                                            )
//                                        }
//                                    }
//                                    // TODO(P2): Purchasing Accounts & Programs
//                                }
//                            }
//                        }
//                    }
//                }
//
//                Row {
//                    Button(
//                        onClick = { viewModel.showNewEntry() },
//                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
//                    ) {
//                        Text("New Entry")
//                    }
//                }
//
//                Spacer(Modifier.height(16.dp))
//
//                SelectionContainer {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End,
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        // Total weight of delivery
//                        val totalWeightPounds = calculateDeliveryTotalWeightPounds(deliveryDetail.data)
//                        Text(
//                            text = "${totalWeightPounds} lbs",
//                            textAlign = TextAlign.End,
//                            fontFamily = FontFamily.Monospace
//                        )
//
//                        Spacer(Modifier.width(32.dp))
//
//                        // Total cost of delivery
//                        val totalCostCents = calculateDeliveryTotalCostCents(deliveryDetail.data)
//                        Text(
//                            text = "$${formatCents(totalCostCents)}",
//                            textAlign = TextAlign.End,
//                            fontFamily = FontFamily.Monospace
//                        )
//                    }
//                }
                }
            }
        }
    }
}