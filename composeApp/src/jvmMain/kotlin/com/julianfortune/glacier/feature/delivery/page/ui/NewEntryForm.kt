package com.julianfortune.glacier.feature.delivery.page.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.ui.common.AutoCompleteDropdownField
import com.julianfortune.glacier.ui.common.CurrencyInput
import com.julianfortune.glacier.ui.common.CurrencyInputTextField
import com.julianfortune.glacier.ui.common.Selection
import com.julianfortune.glacier.ui.common.data.Option

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryForm(
    title: String,
    submitButtonText: String,
    items: List<Entity<Item>>,
    initialEntry: Entry? = null,
    onCancel: () -> Unit,
    onSubmit: (entry: Entry) -> Unit
) {
    var selectedItem by remember { mutableStateOf<Option<Long>?>(null) }

    var unitName by remember { mutableStateOf(initialEntry?.unitName ?: "") }
    var unitWeightPoundsInput by remember {
        mutableStateOf(
            initialEntry?.unitWeight?.toImperial()?.first?.toString() ?: ""
        )
    }
    var unitWeightOuncesInput by remember {
        mutableStateOf(
            initialEntry?.unitWeight?.toImperial()?.second?.toString() ?: ""
        )
    }

    var costStatusIsNoCost by remember { mutableStateOf((initialEntry?.costStatus == CostStatus.NO_COST)) }
    var unitCostInput by remember { mutableStateOf(initialEntry?.unitCostCents?.let { CurrencyInput.fromLong(it) }) }

    var unitCountInput by remember { mutableStateOf(initialEntry?.unitCount?.toString() ?: "") }

    // Parsed and valid values
    var unitCount by remember { mutableStateOf<Long?>(null) }
    var unitCostCents by remember { mutableStateOf<Long?>(null) }
    var itemsPerUnit by remember { mutableStateOf<Long?>(null) }
    var unitWeight by remember { mutableStateOf<Weight?>(null) }

    // Update the selected item once the `items` state populates
    LaunchedEffect(initialEntry, items) {
        selectedItem = initialEntry?.itemId?.let { initialItemId ->
            items.find { it.id == initialEntry.itemId }?.let { item ->
                Option(initialItemId, item.data.name)
            }
        }
    }

    LaunchedEffect(unitCountInput) {
        unitCount = unitCountInput.toLongOrNull()
    }

    LaunchedEffect(unitCostInput) {
        unitCostCents = unitCostInput?.toLong()
    }

    LaunchedEffect(unitWeightPoundsInput, unitWeightOuncesInput) {
        unitWeight = unitWeightPoundsInput.toIntOrNull()?.let { pounds ->
            val ounces = unitWeightOuncesInput.toFloatOrNull() ?: 0f
            Weight.ofImperial(pounds, ounces)
        }
    }

    val isValid = remember(selectedItem, unitWeight, unitCount, costStatusIsNoCost, unitCostCents) {
        val costIsValid = costStatusIsNoCost || unitCostCents != null

        selectedItem != null && unitWeight != null && unitCount != null && costIsValid
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Column {
            AutoCompleteDropdownField(
                selectedOptionId = selectedItem?.id,
                options = items.map {
                    Option(it.id, it.data.name)
                },
                onSelectedChange = { newItem ->
                    selectedItem = newItem
                },
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Unit",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = unitName,
                    onValueChange = { unitName = it },
                    label = { Text("Name") },
                    modifier = Modifier.height(64.dp).weight(2f).onFocusChanged({ state ->
                        if (!state.isFocused) {
                            // Check for error
                        }
                    }),
                    singleLine = true,
                    isError = false,
                    colors = OutlinedTextFieldDefaults.colors(),
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = unitWeightPoundsInput,
                    onValueChange = { unitWeightPoundsInput = it },
                    label = { Text("Lbs") },
                    modifier = Modifier
                        .height(64.dp)
                        .weight(1f) // Takes up half the available space
                        .onFocusChanged({ state ->
                            if (!state.isFocused) {
                                // Check for error
                            }
                        }),
                    singleLine = true,
                    isError = false,
                    colors = OutlinedTextFieldDefaults.colors(),
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = unitWeightOuncesInput,
                    onValueChange = { unitWeightOuncesInput = it },
                    label = { Text("Oz") },
                    modifier = Modifier
                        .height(64.dp)
                        .weight(1f) // Takes up half the available space
                        .onFocusChanged({ state ->
                            if (!state.isFocused) {
                                // Check for error
                            }
                        }),
                    singleLine = true,
                    isError = false,
                    colors = OutlinedTextFieldDefaults.colors(),
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val noCostOption = Option(id = true, title = "No Cost")
                val paidOption = Option(id = false, title = "Paid")

                Selection(
                    options = listOf(paidOption, noCostOption),
                    selectedId = costStatusIsNoCost,
                    onSelectedChange = { selection ->
                        costStatusIsNoCost = selection.id
                    },
                    modifier = Modifier.weight(0.3f).padding(0.dp, 8.dp, 0.dp, 0.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CurrencyInputTextField(
                    label = { Text("Price") },
                    value = unitCostInput,
                    onValueChange = { unitCostInput = it },
                    onFocusLost = {
                        // Simplify the cost if possible
                        unitCostInput = unitCostInput?.toSimplifiedForm()
                    },
                    enabled = !costStatusIsNoCost,
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
                value = unitCountInput,
                onValueChange = { unitCountInput = it },
                label = { Text("Count") },
                modifier = Modifier.height(64.dp).fillMaxWidth().onFocusChanged({ state ->
                    if (!state.isFocused) {
                        // Check for error
                    }
                }),
                singleLine = true,
                isError = false,
                colors = OutlinedTextFieldDefaults.colors(),
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    onCancel()
                }) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                enabled = isValid,
                // TODO(P3): Refactor handler to live somewhere else
                onClick = {
                    val costStatus = when {
                        costStatusIsNoCost -> CostStatus.NO_COST
                        else -> CostStatus.PURCHASED
                    }
                    val costCents = when {
                        costStatusIsNoCost -> 0L
                        else -> unitCostCents!!
                    }
                    val entry = Entry(
                        selectedItem!!.id,
                        unitCount!!,
                        unitName,
                        unitWeight!!,
                        costStatus,
                        costCents,
                        null, // TODO(P1): Item weight
                        itemsPerUnit,
                        null,
                        null,
                    )

                    onSubmit(entry)
                },
            ) {
                Text(submitButtonText)
            }
        }
    }
}