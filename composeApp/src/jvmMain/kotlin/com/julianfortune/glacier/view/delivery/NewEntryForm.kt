package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.CurrencyInput
import com.julianfortune.glacier.view.CurrencyInputTextField
import com.julianfortune.glacier.view.Selection
import com.julianfortune.glacier.view.data.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryForm(
    viewModel: DeliveryViewModel,
    title: String,
    submitButtonText: String,
    initialEntry: Entry? = null,
    onSubmit: (entry: Entry) -> Unit
) {
    val items by viewModel.allItems.collectAsState()

    var selectedItem by remember { mutableStateOf<Option<Long>?>(null) }

    var unitName by remember { mutableStateOf(initialEntry?.unitName ?: "") }
    var unitWeightPoundsInput by remember { mutableStateOf(initialEntry?.unitWeight?.toImperial()?.first?.toString() ?: "") }
    var unitWeightOuncesInput by remember { mutableStateOf(initialEntry?.unitWeight?.toImperial()?.second?.toString() ?: "") }

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
                Option(initialItemId, renderItemName(item.data))
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
                selectedOption = selectedItem, options = items.map {
                    Option(it.id, renderItemName(it.data))
                }, onSelectedChange = { newItem ->
                    selectedItem = newItem
                }, label = { Text("Item") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedCard(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = CardDefaults.cardColors().containerColor
                )
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(
                        text = "Unit",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = unitName,
                        onValueChange = { unitName = it },
                        label = { Text("Name") },
                        modifier = Modifier.height(64.dp).fillMaxWidth().onFocusChanged({ state ->
                            if (!state.isFocused) {
                                // Check for error
                            }
                        }),
                        singleLine = true,
                        isError = false,
                        colors = OutlinedTextFieldDefaults.colors(),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
                    ) {
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val noCostOption = Option(id = true, title = "No Cost")
                val paidOption = Option(id = false, title = "Paid")

                Selection(
                    options = listOf(paidOption, noCostOption),
                    selectedOption = if (costStatusIsNoCost) noCostOption else paidOption,
                    onSelectedChange = { selection ->
                        costStatusIsNoCost = selection.id
                    },
                    modifier = Modifier.weight(0.3f).padding(0.dp, 8.dp, 0.dp, 0.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CurrencyInputTextField(
                    label = { Text("Cost (per unit)") },
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
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    viewModel.dismissEntryModal()
                }) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                enabled = isValid,
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
                        itemsPerUnit,
                        costStatus,
                        costCents,
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