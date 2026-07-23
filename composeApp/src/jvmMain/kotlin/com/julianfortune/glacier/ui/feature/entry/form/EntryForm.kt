package com.julianfortune.glacier.ui.feature.entry.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.input.AutocompleteSelect
import com.julianfortune.glacier.ui.common.input.CurrencyInput
import com.julianfortune.glacier.ui.common.input.CurrencyInputTextField
import com.julianfortune.glacier.ui.common.input.DropdownSelect
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryBody
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryFormState
import com.julianfortune.glacier.ui.theme.AppPreview


@Composable
fun EntryForm(
    title: String,
    submitButtonText: String,
    itemOptions: List<Option<Long>>,
    initialEntry: EntryBody? = null,
    onCancel: () -> Unit,
    onSubmit: (body: EntryBody) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateHolder = remember(initialEntry) {
        EntryFormStateHolder(initialEntry)
    }

    NewEntryFormUi(
        title = title,
        submitButtonText = submitButtonText,
        itemOptions = itemOptions,
        state = stateHolder.uiState,
        onSelectedItemIdChange = stateHolder::onSelectedItemIdChange,
        onWeightPoundsChange = stateHolder::onWeightPoundsChange,
        onWeightOuncesChange = stateHolder::onWeightOuncesChange,
        onCostStatusChange = stateHolder::onCostStatusChange,
        onUnitCostChange = stateHolder::onUnitCostChange,
        onUnitCostFocusLost = stateHolder::onUnitCostFocusLost,
        onUnitCountChange = stateHolder::onUnitCountChange,
        onCancel = onCancel,
        onSubmit = {
            stateHolder.validData?.let { onSubmit(it) }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryFormUi(
    title: String,
    submitButtonText: String,
    itemOptions: List<Option<Long>>,
    state: EntryFormState,
    onSelectedItemIdChange: (Long?) -> Unit = {},
    onWeightPoundsChange: (String) -> Unit = {},
    onWeightOuncesChange: (String) -> Unit = {},
    onCostStatusChange: (Boolean) -> Unit = {},
    onUnitCostChange: (CurrencyInput?) -> Unit = {},
    onUnitCostFocusLost: () -> Unit = {},
    onUnitCountChange: (String) -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Column {
            AutocompleteSelect(
                selectedOptionId = state.selectedItemId.value,
                options = itemOptions,
                onSelectedChange = { onSelectedItemIdChange(it?.id) },
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth()
            )

            // TODO(#29): Select the size of item
            AnimatedVisibility(
                false,
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    DropdownSelect(
                        selectedId = 0,
                        label = "Size",
                        options = listOf(Option(0, "8oz"), Option(1, "12oz"), Option(2, "Other")),
                        onSelectedChange = {  },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Unit (e.g., Case)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.unitWeightPounds.value,
                    onValueChange = onWeightPoundsChange,
                    label = { Text("Lbs") },
                    modifier = Modifier
                        .height(64.dp)
                        .weight(1f)
                        .onFocusChanged { state ->
                            if (!state.isFocused) {
                                // Check for error
                            }
                        },
                    singleLine = true,
                    isError = state.unitWeightPounds.isError,
                    colors = OutlinedTextFieldDefaults.colors(),
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = state.unitWeightOunces.value,
                    onValueChange = onWeightOuncesChange,
                    label = { Text("Oz") },
                    modifier = Modifier
                        .height(64.dp)
                        .weight(1f)
                        .onFocusChanged { state ->
                            if (!state.isFocused) {
                                // Check for error
                            }
                        },
                    singleLine = true,
                    isError = state.unitWeightOunces.isError,
                    colors = OutlinedTextFieldDefaults.colors(),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val noCostOption = Option(id = true, title = "No Cost")
                val paidOption = Option(id = false, title = "Paid")

                DropdownSelect(
                    options = listOf(paidOption, noCostOption),
                    selectedId = state.costStatusIsNoCost.value,
                    onSelectedChange = { selection ->
                        onCostStatusChange(selection.id)
                    },
                    modifier = Modifier.weight(0.3f).padding(0.dp, 8.dp, 0.dp, 0.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CurrencyInputTextField(
                    label = { Text("Price") },
                    value = state.unitCost.value,
                    onValueChange = onUnitCostChange,
                    onFocusLost = onUnitCostFocusLost,
                    enabled = !state.costStatusIsNoCost.value,
                    modifier = Modifier.weight(0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quantity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.unitCount.value,
                onValueChange = onUnitCountChange,
                label = { Text("Count") },
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        if (!state.isFocused) {
                            // Check for error
                        }
                    },
                singleLine = true,
                isError = state.unitCount.isError,
                colors = OutlinedTextFieldDefaults.colors(),
            )

            // TODO(!!): Add program and purchasing account dropdowns
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onCancel
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = state.isValid,
                onClick = onSubmit,
            ) {
                Text(submitButtonText)
            }
        }
    }
}

@Preview
@Composable
fun EntryFormPreview() {
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            NewEntryFormUi(
                title = "New Entry",
                submitButtonText = "Save",
                itemOptions = listOf(Option(1L, "Placeholder Item")),
                state = EntryFormState()
            )
        }
    }
}