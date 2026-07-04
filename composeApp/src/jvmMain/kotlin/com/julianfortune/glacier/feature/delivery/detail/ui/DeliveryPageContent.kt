package com.julianfortune.glacier.feature.delivery.detail.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.feature.delivery.detail.data.DeliveryContentState
import com.julianfortune.glacier.feature.delivery.detail.data.EntryRowState
import com.julianfortune.glacier.ui.common.EntityOptionsDropdownMenu
import com.julianfortune.glacier.ui.theme.AppPreview
import com.julianfortune.glacier.ui.theme.dynamicScrollbarStyle


@Composable
fun DeliveryPageContent(
    state: DeliveryContentState,
    onClickEditDetails: () -> Unit,
    onClickAddEntry: () -> Unit,
    onClickEditEntry: (entryId: Long) -> Unit,
    onClickDeleteEntry: (entryId: Long) -> Unit,
) {
    val contentMaxWidth = 960.dp
    val horizontalContentPadding = 24.dp
    val horizontalTextPadding = 16.dp

    // TODO(P1): Create a reusable scroll column
    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = contentMaxWidth)
                    .verticalScroll(scrollState),
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Delivery 'header' data section
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = horizontalContentPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = horizontalTextPadding)
                        )

                        Row {
                            IconButton(
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                onClick = onClickEditDetails
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit details"
                                )
                            }
                        }
                    }

                    KeyValueList(
                        modifier = Modifier.widthIn(max = 360.dp).padding(horizontal = horizontalTextPadding)
                    ) {
                        KeyValueItem(Icons.Outlined.CalendarMonth, "Received", state.receivedDate)
                        KeyValueItem(Icons.Outlined.Storefront, "Supplier", state.supplierName)
                        KeyValueItem(Icons.Outlined.Gavel, "Taxes", state.taxes, FontFamily.Monospace)
                        KeyValueItem(Icons.Outlined.CreditCard, "Fees", state.fees, FontFamily.Monospace)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val rowWeights = { column: String ->
                    when (column) {
                        "item" -> 2f
                        "program" -> 1.5f
                        "account" -> 1.5f
                        "cost" -> 1f
                        else -> 0.8f
                    }
                }

                val spaceBetweenCells = 16.dp

                Column(
                    modifier = Modifier
                        .padding(horizontal = horizontalContentPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Entries",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = horizontalTextPadding)
                        )
                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = onClickAddEntry
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Entry",
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalTextPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spaceBetweenCells)
                    ) {
                        Text(
                            text = "Item",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.weight(rowWeights("item")),
                        )
                        Text(
                            text = "Program",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.weight(rowWeights("program")),
                        )
                        Text(
                            text = "Account",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.weight(rowWeights("account")),
                        )
                        Text(
                            text = "Count",
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.weight(rowWeights("count")),
                        )
                        Text(
                            text = "Weight",
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.weight(rowWeights("weight")),
                        )
                        Text(
                            text = "Cost",
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.weight(rowWeights("cost")),
                        )
                        Row(
                            modifier = Modifier
                                .width(24.dp)
                                .background(Color.Red)
                        ) { }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }

                state.entryRows.forEach { entryRow ->
                    Column(modifier = Modifier.padding(horizontal = horizontalContentPadding)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = horizontalTextPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spaceBetweenCells)
                        ) {
                            Text(
                                text = entryRow.itemName,
                                modifier = Modifier.weight(rowWeights("item")),
                            )
                            Text(
                                text = entryRow.programName ?: "",
                                modifier = Modifier.weight(rowWeights("program")),
                            )
                            Text(
                                text = entryRow.accountName ?: "",
                                modifier = Modifier.weight(rowWeights("account")),
                            )
                            Text(
                                text = entryRow.unitCount,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(rowWeights("count")),
                            )
                            Text(
                                text = entryRow.totalWeight,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(rowWeights("weight")),
                            )
                            Text(
                                text = entryRow.totalCost,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(rowWeights("cost")),
                            )
                            Row(modifier = Modifier.width(24.dp)) {
                                EntityOptionsDropdownMenu(
                                    edit = { onClickEditEntry(entryRow.entryId) },
                                    delete = { onClickDeleteEntry(entryRow.entryId) }
                                )
                            }
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = horizontalContentPadding)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable { onClickAddEntry() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "New",
                        Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = horizontalContentPadding),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = horizontalTextPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spaceBetweenCells)
                    ) {
                        Text(
                            text = "",
                            modifier = Modifier.weight(rowWeights("item")),
                        )
                        Text(
                            text = "",
                            modifier = Modifier.weight(rowWeights("program")),
                        )
                        Text(
                            text = "",
                            modifier = Modifier.weight(rowWeights("account")),
                        )
                        Text(
                            text = state.totalCount,
                            textAlign = TextAlign.End,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(rowWeights("count")),
                        )
                        Text(
                            text = state.totalWeight,
                            textAlign = TextAlign.End,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(rowWeights("weight")),
                        )
                        Text(
                            text = state.totalCost,
                            textAlign = TextAlign.End,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(rowWeights("cost")),
                        )
                        Row(modifier = Modifier.width(24.dp)) { }
                    }
                }

                if (state.entryRows.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalContentPadding)) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp, top = 24.dp, 16.dp, bottom = 8.dp)
                        )
                        Spacer(Modifier.height(12.dp))

                        KeyValueList(
                            modifier = Modifier
                                .widthIn(max = 360.dp)
                                .padding(horizontal = horizontalTextPadding)
                        ) {
                            KeyValueItem(
                                Icons.Outlined.Summarize, "Subtotal", state.subtotal, FontFamily.Monospace
                            )
                            KeyValueItem(
                                Icons.Outlined.Gavel, "Taxes", state.taxes, FontFamily.Monospace
                            )
                            KeyValueItem(
                                Icons.Outlined.CreditCard, "Fees", state.fees, FontFamily.Monospace
                            )
                            KeyValueItem(
                                Icons.Outlined.Money, "Total", state.totalCost, FontFamily.Monospace
                            )

                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(2.dp),
            adapter = rememberScrollbarAdapter(scrollState),
            style = dynamicScrollbarStyle(MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Preview
@Composable
fun DeliveryPageContentPreview() {
    AppPreview {
        DeliveryPageContent(
            DeliveryContentState(
                "09/10/2998",
                "ABC Foods",
                listOf(
                    EntryRowState(1, "Green Beans", null, null, "4", "40.0", "$28"),
                    EntryRowState(2, "Lettuce", null, null, "7", "70.0", "$43"),
                ),
                "2",
                "320.0",
                "$800.00",
                "$0.00",
                "$0.00",
                "$800.00",
            ),
            {},
            onClickEditEntry = {},
            onClickDeleteEntry = {},
            onClickAddEntry = {},
        )
    }
}