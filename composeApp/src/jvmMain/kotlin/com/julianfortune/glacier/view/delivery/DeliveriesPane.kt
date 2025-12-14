package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.view.Item
import com.julianfortune.glacier.view.ScrollableColumn
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.flow.map
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
    val dialogIsOpen by viewModel.newDeliveryDialogIsVisible

    if (dialogIsOpen) {
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
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                ) {
                    Text(
                        deliveryDetail?.data?.receivedDate?.let { formatLocalDate(it, FormatStyle.FULL) }.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Text("Order ID: ${deliveryDetail?.id} ...",)
                    Text("Supplier ID: ${deliveryDetail?.data?.supplierId}")

                    // TODO(P2):Make editable
                    Text("Fees: ${deliveryDetail?.data?.feesCents?.let { it / 100 }}")

                    // TODO(NEXT): Display editable entries list
                    //  -> focus first on editing and saving functionality before any UX upgrades

//                entries.mapIndexed { index, entry ->
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                        modifier = Modifier,
//                    ) {
//                        Text("${index + 1}")
//                        AutoCompleteDropdownField(
//                            label = { Text("Item") },
//                            options = items.map {
//                                Option(it.id, it.data.name)
//                            },
//                            onSelectedChange = { newId ->
//                                println("Selected: $newId")
//                            }
//                        )
//                        OutlinedTextField(
//                            value = firstItemCount,
//                            onValueChange = { firstItemCount = it },
//                            label = { Text("Count") },
//                            singleLine = true,
//                            modifier = Modifier.height(64.dp), // Fixes a dumb glitch where the height changes
//                            colors = OutlinedTextFieldDefaults.colors()
//                        )
//                        // TODO: CostStatus dropdown
//                        IconButton(
//                            modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
//                            onClick = {
//                                TODO("Delete entry")
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = "Delete entry"
//                            )
//                        }
//                    }
//                }
//
//                Row {
//                    Button(
//                        onClick = {
//                            TODO("Add new entry")
//                        }
//                    ) {
//                        Text("New Entry")
//                    }
//                }
                }
            }

        }
    }
}