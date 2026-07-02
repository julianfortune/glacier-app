package com.julianfortune.glacier.feature.delivery.page.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
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
import com.julianfortune.glacier.feature.delivery.page.data.*
import com.julianfortune.glacier.ui.common.EntityOptionsDropdownMenu
import com.julianfortune.glacier.ui.theme.AppPreview
import com.julianfortune.glacier.ui.theme.dynamicScrollbarStyle


@Composable
fun DeliveryPageContent(
    state: DeliveryPageState,
    onClickEditDetails: () -> Unit,
    onClickEditEntry: (entryId: Long) -> Unit,
    onClickDeleteEntry: (index: Int) -> Unit,
    onClickAddEntry: () -> Unit,
) {
    val contentMaxWidth = 960.dp
    val horizontalContentPadding = 24.dp
    val horizontalTextPadding = 16.dp

    Box(modifier = Modifier.fillMaxSize()) {
        val listScrollState = rememberLazyListState()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = contentMaxWidth),
                state = listScrollState,
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
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

                        // TODO: Probably should be handled by ViewModel ..?
                        val detailItems = listOf(
                            KeyValueItem(Icons.Outlined.CalendarMonth, "Received", state.details.receivedDate),
                            KeyValueItem(Icons.Outlined.Storefront, "Supplier", state.details.supplierName),
                            KeyValueItem(Icons.Outlined.Gavel, "Taxes", state.details.taxes, FontFamily.Monospace),
                            KeyValueItem(Icons.Outlined.CreditCard, "Fees", state.details.fees, FontFamily.Monospace),
                        )

                        KeyValueList(
                            detailItems,
                            modifier = Modifier.padding(horizontal = horizontalTextPadding).widthIn(max = 320.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

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

                stickyHeader {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = horizontalContentPadding)
                    ) {
                        Spacer(Modifier.height(4.dp))
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = horizontalTextPadding,
                                    end = horizontalTextPadding,
                                    bottom = 16.dp,
                                    top = 4.dp
                                ),
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
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )
                    }
                }

                items(state.entry.entryRows) { entryRow ->
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
                                    edit = {
                                        // TODO ...
                                    },
                                    delete = {
                                        // TODO ...
                                    }
                                )
                            }
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )
                    }
                }

                item {
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
                }

                item {
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
                                text = state.entry.totalCount,
                                textAlign = TextAlign.End,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(rowWeights("count")),
                            )
                            Text(
                                text = state.entry.totalWeight,
                                textAlign = TextAlign.End,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(rowWeights("weight")),
                            )
                            Text(
                                text = state.entry.totalCost,
                                textAlign = TextAlign.End,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(rowWeights("cost")),
                            )
                            Row(modifier = Modifier.width(24.dp)) { }
                        }
                    }
                }

                if (state.entry.entryRows.size > 0) {
                    item {
                        Column(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalContentPadding)) {
                            Text(
                                text = "Summary",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp, top = 24.dp, 16.dp, bottom = 8.dp)
                            )
                            Spacer(Modifier.height(12.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = horizontalTextPadding),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                // TODO: Probably should be handled by ViewModel ..?
                                val summaryItems = listOf(
                                    KeyValueItem(
                                        Icons.Outlined.Summarize,
                                        "Subtotal",
                                        state.summary.subtotal,
                                        FontFamily.Monospace
                                    ),
                                    KeyValueItem(
                                        Icons.Outlined.Gavel,
                                        "Taxes",
                                        state.summary.taxes,
                                        FontFamily.Monospace
                                    ),
                                    KeyValueItem(
                                        Icons.Outlined.CreditCard,
                                        "Fees",
                                        state.summary.fees,
                                        FontFamily.Monospace
                                    ),
                                    KeyValueItem(
                                        Icons.Outlined.Money,
                                        "Total",
                                        state.summary.total,
                                        FontFamily.Monospace
                                    ),
                                )

                                KeyValueList(
                                    summaryItems,
                                    modifier = Modifier.widthIn(max = 240.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(2.dp),
            adapter = rememberScrollbarAdapter(listScrollState),
            style = dynamicScrollbarStyle(MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Preview
@Composable
fun DeliveryPageContentPreview() {
    AppPreview {
        DeliveryPageContent(
            DeliveryPageState(
                details = DeliveryPageDetailsState(
                    "09/10/2998",
                    "ABC Foods",
                    "$0.00",
                    "$0.00",
                ),
                entry = DeliveryPageEntryState(
                    listOf(
                        EntryRowState("Green Beans", null, null, "4", "40.0", "$28"),
                        EntryRowState("Lettuce", null, null, "7", "70.0", "$43")
                    ),
                    totalCount = "2",
                    totalWeight = "320.0",
                    totalCost = "$800.00",
                ),
                summary = DeliveryPageSummaryState(
                    "$800.00",
                    "$0.00",
                    "$0.00",
                    "$800.00",
                ),
            ),
            {},
            onClickEditEntry = {},
            onClickDeleteEntry = {},
            onClickAddEntry = {},
        )
    }
}