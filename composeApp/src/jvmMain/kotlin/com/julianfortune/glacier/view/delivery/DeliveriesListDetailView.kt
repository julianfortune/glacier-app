package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.view.Item
import com.julianfortune.glacier.view.ScrollableColumn
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun formatLocalDate(d: LocalDate, style: FormatStyle = FormatStyle.MEDIUM): String {
    val usDateFormatter = DateTimeFormatter.ofLocalizedDate(style).withLocale(Locale.US)
    return d.format(usDateFormatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesPane(viewModel: DeliveryViewModel) {
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
    val newDeliveryDialogOpen by viewModel.newDeliveryDialogIsVisible
    val newEntryDialogOpen by viewModel.newEntryDialogIsVisible

    if (newDeliveryDialogOpen) {
        BasicAlertDialog(
            onDismissRequest = { }, // Ignore implicit attempts to close the dialog
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                NewDeliveryForm(viewModel)
            }
        }
    }

    if (newEntryDialogOpen) {
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
                NewEntryForm(viewModel, selectedDeliveryId!!)
            }
        }
    }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Column(modifier = Modifier.width(240.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(8.dp),
                ) {
                    FilledTonalButton(
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.height(32.dp).fillMaxWidth(),
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        Text(
                            deliveryDetail?.data?.receivedDate?.let { formatLocalDate(it, FormatStyle.FULL) }.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        // TODO(P3): Make supplier editable
                        Text("Supplier: ${supplierMap[deliveryDetail?.data?.supplierId]?.data?.name}")

                        // TODO(P1): Make editable
                        Text("Fees: ${deliveryDetail?.data?.feesCents?.let { it / 100 }}")

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Entries",
                            style = MaterialTheme.typography.headlineSmall,
                        )

                        // TODO(NEXT): Display editable entries list
                        //  -> focus first on editing and saving functionality before any UX upgrades
                        //  -> just go with a simple column with dividers or something standard
                        //  -> use an `...` menu for all the actions (e.g., `Edit`, `Delete`)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(vertical = 24.dp)
                        ) {
                            val entries = deliveryDetail?.data?.entries
                            when {
                                entries == null || entries.isEmpty() -> Text("None")
                                else -> {
                                    entries.mapIndexed { index, entry ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text(
                                                text = itemMap[entry.itemId]?.data?.name ?: "...",
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Text(
                                                text = "${entry.itemCount}",
                                                modifier = Modifier.width(60.dp),
                                                textAlign = TextAlign.End
                                            )

                                            Text(
                                                text = "${entry.itemCostCents}",
                                                modifier = Modifier.width(80.dp),
                                                textAlign = TextAlign.End
                                            )

                                            // TODO(P1): Ellipses menu with actions: edit, delete, move up, move down

                                            // TODO(P3): Make each 'entry' item directly editable
                                        }
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
                    }
                }
            }

        }
    }
}