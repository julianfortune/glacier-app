package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.util.formatCents
import com.julianfortune.glacier.view.shared.EntityOptionsDropdownMenu
import com.julianfortune.glacier.viewModel.DeliveryViewModel
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

@Composable
fun DeliveryView(
    deliveryDetail: Entity<DeliveryDetail>,
    itemMap: Map<Long, Entity<Item>>,
    supplierMap: Map<Long, Entity<Supplier>>,
    viewModel: DeliveryViewModel
) {
    val coroutineScope = rememberCoroutineScope()

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
                Column {
                    Spacer(Modifier.height(4.dp)) // Better align with icon buttons
                    Text(
                        formatLocalDate(deliveryDetail.data.receivedDate, FormatStyle.FULL),
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Text("${supplierMap[deliveryDetail.data.supplierId]?.data?.name}")
                    Text("Fees: $${formatCents(deliveryDetail.data.feesCents ?: 0)}")
                    Text("Taxes: $${formatCents(deliveryDetail.data.taxesCents ?: 0)}")
                }
                Row {
                    IconButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        onClick = {
                            viewModel.showEditDelivery(deliveryDetail)
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
                            viewModel.showDeleteDelivery(deliveryDetail)
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

                val entries = deliveryDetail.data.entries
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
                                    EntityOptionsDropdownMenu(
                                        edit = {
                                            viewModel.showEditEntry(index, entry)
                                        },
                                        delete = {
                                            // TODO(P4): Revisit this code to try to make more fluent
                                            val currentDeliveryDetail: Entity<DeliveryDetail> = deliveryDetail

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
            val totalCostCents = calculateDeliveryTotalCostCents(deliveryDetail.data)
            Text(
                text = "$${formatCents(totalCostCents)}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}