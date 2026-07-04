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
import java.beans.ConstructorProperties


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

                    EntryRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ItemNameCell { EntryRowHeaderText("Item") }
                        ProgramCell { EntryRowHeaderText("Program") }
                        PurchasingAccountCell { EntryRowHeaderText("Account") }
                        UnitCountCell { EntryRowHeaderText("Count") }
                        EntryWeightCell { EntryRowHeaderText("Weight") }
                        EntryCostCell { EntryRowHeaderText("Cost") }
                        ActionCell { }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }

                Column(modifier = Modifier.padding(horizontal = horizontalContentPadding)) {
                    state.entryRows.forEach { entryRow ->
                        EntryRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 12.dp,
                                ),
                        ) {
                            ItemNameCell { Text(entryRow.itemName) }
                            ProgramCell { Text(entryRow.programName ?: "") }
                            PurchasingAccountCell { Text(entryRow.accountName ?: "") }
                            UnitCountCell {
                                Text(
                                    text = entryRow.unitCount,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                            EntryWeightCell {
                                Text(
                                    text = entryRow.totalWeight,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                            EntryCostCell {
                                Text(
                                    text = entryRow.totalCost,
                                    textAlign = TextAlign.End,
                                )
                            }
                            ActionCell {
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
                    EntryRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 12.dp,
                            ),
                    ) {
                        ItemNameCell {  }
                        ProgramCell {  }
                        PurchasingAccountCell { }
                        UnitCountCell {
                            Text(
                                text = state.totalCount,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        EntryWeightCell {
                            Text(
                                text = state.totalWeight,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        EntryCostCell {
                            Text(
                                text = state.totalCost,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        ActionCell { }
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

@Composable
fun EntryRowHeaderText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    )
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