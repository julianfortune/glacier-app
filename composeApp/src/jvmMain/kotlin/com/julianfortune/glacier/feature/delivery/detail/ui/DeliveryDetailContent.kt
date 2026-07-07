package com.julianfortune.glacier.feature.delivery.detail.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.feature.delivery.detail.data.DeliveryContentState
import com.julianfortune.glacier.feature.delivery.detail.data.EntryRowState
import com.julianfortune.glacier.ui.common.EntityOptionsDropdownMenu
import com.julianfortune.glacier.ui.theme.dynamicScrollbarStyle


private val contentMaxWidth = 960.dp
private val horizontalContentPadding = 24.dp
private val horizontalTextPadding = 16.dp

@Composable
fun DeliveryPageContent(
    state: DeliveryContentState,
    onClickEditDetails: () -> Unit,
    onClickAddEntry: () -> Unit,
    onClickEditEntry: (entryId: Long) -> Unit,
    onClickDeleteEntry: (entryId: Long) -> Unit,
    onClickToggleAllEntries: () -> Unit,
    onClickToggleEntry: (isSelected: Boolean, entryId: Long) -> Unit,
    onClickClearEntrySelection: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = contentMaxWidth)
                    .verticalScroll(scrollState)
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                DeliveryHeader(
                    state.receivedDate,
                    state.supplierName,
                    state.taxes,
                    state.fees,
                    onClickEditDetails,
                )

                EntriesTable(
                    state.entrySelectionCount,
                    state.entrySelectionState,
                    state.entryRows,
                    state.totalCount,
                    state.totalWeight,
                    state.totalCost,
                    onClickAddEntry,
                    onClickEditEntry,
                    onClickDeleteEntry,
                    onClickToggleAllEntries,
                    onClickToggleEntry,
                    onClickClearEntrySelection,
                )

                if (state.entryRows.isNotEmpty()) {
                    DeliverySummary(
                        state.subtotal,
                        state.taxes,
                        state.fees,
                        state.totalCost,
                    )
                }

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
fun DeliveryHeader(
    receivedDate: String,
    supplierName: String,
    taxes: String,
    fees: String,
    onClickEdit: () -> Unit,
) {
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
                    onClick = onClickEdit
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
            KeyValueItem(Icons.Outlined.CalendarMonth, "Received", receivedDate)
            KeyValueItem(Icons.Outlined.Storefront, "Supplier", supplierName)
            KeyValueItem(Icons.Outlined.Gavel, "Taxes", taxes, FontFamily.Monospace)
            KeyValueItem(Icons.Outlined.CreditCard, "Fees", fees, FontFamily.Monospace)
        }
    }
}

@Composable
fun EntriesTable(
    entrySelectionCount: Int,
    entrySelectionState: ToggleableState,
    entryRows: List<EntryRowState>,
    totalCount: String,
    totalWeight: String,
    totalCost: String,
    onClickAddEntry: () -> Unit,
    onClickEditEntry: (entryId: Long) -> Unit,
    onClickDeleteEntry: (entryId: Long) -> Unit,
    onClickToggleAllEntries: () -> Unit,
    onClickToggleEntry: (isSelected: Boolean, entryId: Long) -> Unit,
    onClickClearEntrySelection: () -> Unit,
) {
    var selectionModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = horizontalContentPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Entries",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = horizontalTextPadding)
            )

            val actionRowHeight = 48.dp

            Box(contentAlignment = Alignment.TopEnd) {
                Column(
                    modifier = Modifier.height(actionRowHeight),
                    verticalArrangement = Arrangement.Top
                ) {
                    Row {
                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = {
                                selectionModeEnabled = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Select multiple entries",
                            )
                        }
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
                }

                Column(
                    modifier = Modifier.height(actionRowHeight),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    AnimatedVisibility(
                        selectionModeEnabled,
                        enter = fadeIn(), // + slideInVertically(initialOffsetY = { -it / 4 }),
                        exit = fadeOut(), //+ slideOutVertically(targetOffsetY = { -it / 4 }),
                    ) {
                        Card(
                            colors = CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                        ) {
                            Row(
                                modifier = Modifier.height(actionRowHeight),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    "$entrySelectionCount selected",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                IconButton(
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                    onClick = {
                                        onClickClearEntrySelection()
                                        selectionModeEnabled = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Cancel selection",
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header row
        EntryRow(
            modifier = Modifier.fillMaxWidth().height(32.dp),
        ) {
            SelectionCell(selectionModeEnabled) {
                TriStateCheckbox(
                    modifier = Modifier
                        .size(28.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                    state = entrySelectionState,
                    onClick = {
                        onClickToggleAllEntries()
                    },
                )
            }
            ItemNameCell { EntryRowHeaderText("Item") }
            ProgramCell { EntryRowHeaderText("Program") }
            PurchasingAccountCell { EntryRowHeaderText("Account") }
            UnitCountCell { EntryRowHeaderText("Count") }
            EntryWeightCell { EntryRowHeaderText("Weight") }
            EntryCostCell { EntryRowHeaderText("Cost") }
            ActionCell { }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )

        entryRows.forEach { entryRow ->
            EntryRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp,
                    ),
            ) {
                SelectionCell(selectionModeEnabled) {
                    Checkbox(
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand),
                        checked = entryRow.isSelected,
                        onCheckedChange = {
                            onClickToggleEntry(it, entryRow.entryId)
                        },
                    )
                }
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
                        fontFamily = FontFamily.Monospace,
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

        Column(
            modifier = Modifier
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

        // Footer
        EntryRow(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp,
                ),
        ) {
            SelectionCell(selectionModeEnabled) {}
            ItemNameCell { }
            ProgramCell { }
            PurchasingAccountCell { }
            UnitCountCell {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "UNITS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
//                        fontFamily = FontFamily.Monospace,
                    )
                    SelectionContainer {
                        Text(
                            text = totalCount,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
            EntryWeightCell {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "LBS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    )
                    SelectionContainer {
                        Text(
                            text = totalWeight,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
            EntryCostCell {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SUBTOTAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    )
                    SelectionContainer {
                        Text(
                            text = totalCost,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
            ActionCell { }
        }
    }
}

@Composable
fun DeliverySummary(
    subtotal: String,
    taxes: String,
    fees: String,
    total: String,
) {
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
                Icons.Outlined.Summarize, "Subtotal", subtotal, FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.Gavel, "Taxes", taxes, FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.CreditCard, "Fees", fees, FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Outlined.Money, "Total", total, FontFamily.Monospace
            )

        }
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

//@Preview
//@Composable
//fun DeliveryPageContentPreview() {
//    AppPreview {
//        DeliveryPageContent(
//            DeliveryContentState(
//                "09/10/2998",
//                "ABC Foods",
//                listOf(
//                    EntryRowState(1, false, "Green Beans", null, null, "4", "40.0", "$28.00"),
//                    EntryRowState(2, false, "Lettuce", null, null, "7", "70.0", "$43.00"),
//                ),
//                "11",
//                "110.0",
//                "$800.00",
//                "$0.00",
//                "$0.00",
//                "$800.00",
//            ),
//            {},
//            onClickEditEntry = {},
//            onClickDeleteEntry = {},
//            onClickAddEntry = {},
//            onClickToggleAllEntries = {},
//            onClickToggleEntry = { _, _ -> },
//        )
//    }
//}
//
//@Preview
//@Composable
//fun DeliveryPageContentMaximalistPreview() {
//    AppPreview {
//        DeliveryPageContent(
//            DeliveryContentState(
//                "12/31/2000",
//                "Abracadabra Foods Incorporated",
//                listOf(
//                    EntryRowState(1, false, "Organic Himalayan Salt", null, null, "100", "4000.0", "$2800.00"),
//                    EntryRowState(2, false, "Organic Fresh Dinosaur Kale", null, null, "7", "70.0", "$43.00"),
//                ),
//                "3000",
//                "10320.0",
//                "$11000.00",
//                "$999.00",
//                "$999.00",
//                "$99999.99",
//            ),
//            {},
//            onClickEditEntry = {},
//            onClickDeleteEntry = {},
//            onClickAddEntry = {},
//            onClickToggleAllEntries = {},
//            onClickToggleEntry = { _, _ -> },
//        )
//    }
//}