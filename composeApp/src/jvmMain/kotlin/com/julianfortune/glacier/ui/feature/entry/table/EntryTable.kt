package com.julianfortune.glacier.ui.feature.entry.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Cases
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.julianfortune.glacier.ui.common.input.AutocompleteSelect
import com.julianfortune.glacier.ui.common.component.ConfirmDeleteEntityForm
import com.julianfortune.glacier.ui.common.foundation.Dialog
import com.julianfortune.glacier.ui.common.component.EntityOptionsDropdownMenu
import com.julianfortune.glacier.ui.feature.entry.form.EntryForm
import com.julianfortune.glacier.ui.feature.entry.table.data.EntryAction
import com.julianfortune.glacier.ui.feature.entry.table.data.EntryTableState
import com.julianfortune.glacier.ui.feature.entry.table.data.SelectionState
import com.julianfortune.glacier.ui.feature.entry.table.ui.EntryRow
import com.julianfortune.glacier.ui.feature.entry.table.ui.EntrySideSheet
import com.julianfortune.glacier.ui.theme.AppPreview
import org.koin.compose.viewmodel.koinViewModel

private val horizontalContentPadding = 24.dp
private val horizontalTextPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryTable(
    viewModel: EntryTableViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val itemOptions by viewModel.itemOptions.collectAsState(emptyList())

    Box {
        uiState?.let { state ->
            EntryTableUi(
                state,
                viewModel::showAddEntry,
                viewModel::showEditEntry,
                viewModel::showDeleteEntry,
                viewModel::enableEntrySelection,
                viewModel::disableEntrySelection,
                viewModel::onToggleAllEntriesSelection,
                viewModel::onToggleEntrySelection,
                viewModel::clearEntrySelection,
            )

            // TODO: Move inside `..Ui()` below
            when (val action = state.action) {
                is EntryAction.Add -> {
                    EntrySideSheet(
                        onClose = { viewModel.cancelEntryOperation() }
                    ) { dismissSheet ->
                        EntryForm(
                            "New Entry",
                            "Create",
                            itemOptions,
                            onCancel = {
                                dismissSheet()
                            },
                            onSubmit = { entry ->
                                viewModel.saveEntry(entry)
                                dismissSheet()
                            }
                        )
                    }
                }

                is EntryAction.Edit -> {
                    EntrySideSheet(
                        onClose = { viewModel.cancelEntryOperation() }
                    ) { dismissSheet ->
                        EntryForm(
                            "Edit Entry",
                            "Update",
                            itemOptions,
                            initialEntry = action.entry,
                            onCancel = {
                                dismissSheet()
                            },
                            onSubmit = { newEntry ->
                                viewModel.updateEntry(action.id, newEntry)
                                dismissSheet()
                            }
                        )
                    }
                }

                is EntryAction.Delete -> {
                    Dialog(
                        onDismissRequest = { viewModel.cancelEntryOperation() },
                    ) {
                        ConfirmDeleteEntityForm(
                            action.id,
                            "Delete Entry",
                            onCancel = {
                                viewModel.cancelEntryOperation()
                            },
                            onConfirm = { id ->
                                viewModel.deleteEntryById(id)
                                viewModel.cancelEntryOperation()
                            }
                        )
                    }
                }

                null -> Unit
            }
        }
    }
}

@Composable
fun EntryTableUi(
    state: EntryTableState,
    onClickAddEntry: () -> Unit = {},
    onClickEditEntry: (entryId: Long) -> Unit = {},
    onClickDeleteEntry: (entryId: Long) -> Unit = {},
    onClickEnableSelection: () -> Unit = {},
    onClickDisableSelection: () -> Unit = {},
    onClickToggleAllEntries: () -> Unit = {},
    onClickToggleEntry: (isSelected: Boolean, entryId: Long) -> Unit = { _, _ -> },
    onClickClearEntrySelection: () -> Unit = {},
) {
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

            when (val selectionState = state.selection) {
                is SelectionState.Disabled -> {
                    Row {
                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = {
                                onClickEnableSelection()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Select multiple entries",
                            )
                        }
                    }
                }

                is SelectionState.Enabled -> {
                    Row(
                        modifier = Modifier.height(actionRowHeight),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "${selectionState.count} selected",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.width(16.dp))

                        SelectionProgramModifierMenu()

                        SelectionAccountModifierMenu()

                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = {
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete entries",
                            )
                        }

                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = {
                                onClickClearEntrySelection()
                                onClickDisableSelection()
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

        Spacer(modifier = Modifier.height(16.dp))

        // Header row
        EntryRow(
            modifier = Modifier.fillMaxWidth().height(32.dp),
        ) {
            val selectAllState = (state.selection as? SelectionState.Enabled)?.selectAll ?: ToggleableState.Off

            SelectionCell(state.selection is SelectionState.Enabled) {
                TriStateCheckbox(
                    modifier = Modifier
                        .size(28.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                    state = selectAllState,
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

        state.rows.forEach { entryRow ->
            EntryRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp,
                    ),
            ) {
                SelectionCell(state.selection is SelectionState.Enabled) {
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
            SelectionCell(state.selection is SelectionState.Enabled) {}
            ItemNameCell { }
            ProgramCell { }
            PurchasingAccountCell { }
            UnitCountCell {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "UNITS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    )
                    SelectionContainer {
                        Text(
                            text = state.totalCount,
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
                            text = state.totalWeight,
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
                            text = state.totalCost,
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
fun EntryRowHeaderText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    )
}

@Composable
fun SelectionProgramModifierMenu() {
    SelectionModifierMenu(
        icon = {
            Icon(
                imageVector = Icons.Outlined.Cases,
                contentDescription = "Edit Program",
            )
        },
        tooltipText = "Edit Program",
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .padding(16.dp)
        ) {
            Text("Edit Program")

            // TODO(#28)
            AutocompleteSelect(
                null,
                emptyList(),
                onSelectedChange = {},
                label = { Text("Program") },
            )

            Button(
                onClick = { },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun SelectionAccountModifierMenu() {
    SelectionModifierMenu(
        icon = {
            Icon(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = "Edit Purchasing Account",
            )
        },
        tooltipText = "Edit Purchasing Account",
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .padding(16.dp)
        ) {
            Text("Edit Account")

            // TODO(#28)
            AutocompleteSelect(
                null,
                emptyList(),
                onSelectedChange = {},
                label = { Text("Account") },
            )

            Button(
                onClick = { },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text("Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionModifierMenu(
    icon: @Composable () -> Unit,
    tooltipText: String,
    content: @Composable () -> Unit
) {
    var programPopoverOpen by remember { mutableStateOf(false) }

    Column {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip { Text(tooltipText) }
            },
            state = rememberTooltipState()
        ) {
            IconButton(
                onClick = { programPopoverOpen = !programPopoverOpen },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                icon()
            }
        }

        Box {
            if (programPopoverOpen) {
                Popup(
                    alignment = Alignment.TopCenter,
                    // Dismisses the popup when clicking outside
                    onDismissRequest = { programPopoverOpen = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OutlinedCard(
                            colors = CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EntryTablePreviewEmpty() = AppPreview {
    EntryTableUi(
        EntryTableState.empty()
    )
}