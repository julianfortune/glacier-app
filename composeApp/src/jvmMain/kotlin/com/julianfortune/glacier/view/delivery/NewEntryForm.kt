package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.CurrencyInput
import com.julianfortune.glacier.view.CurrencyInputTextField
import com.julianfortune.glacier.view.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel

fun renderItemName(item: Item): String {
    // TODO(P1): Implement weight conversion (P3 based on user preference)
    val weight = if (item.weightGrams != null) {
        "(${item.weightGrams}g)"
    } else null
    val description = if (item.description != null) {
        "— ${item.description}"
    } else null
    return listOfNotNull(item.name, description, weight).joinToString(" ")
}

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
    var itemCountInput by remember { mutableStateOf(initialEntry?.unitCount?.toString() ?: "") }
    var itemCostInput by remember { mutableStateOf(initialEntry?.unitCostCents?.let { CurrencyInput.fromLong(it) }) }
    var costStatusIsNoCost by remember { mutableStateOf((initialEntry?.costStatus == CostStatus.NO_COST) ?: false) }

    // Parsed and valid values
    var itemCount by remember { mutableStateOf<Long?>(null) }
    var itemCostCents by remember { mutableStateOf<Long?>(null) }

    // Update the selected item once the `items` state populates
    LaunchedEffect(initialEntry, items) {
        selectedItem = initialEntry?.itemId?.let { initialItemId ->
            items.find { it.id == initialEntry.itemId }?.let { item ->
                Option(initialItemId, item.data.name)
            }
        }
    }

    LaunchedEffect(itemCountInput) {
        itemCount = itemCountInput.toLongOrNull()
    }

    LaunchedEffect(itemCostInput) {
        itemCostCents = itemCostInput?.toLong()
    }

    val isValid = remember(selectedItem, itemCount, costStatusIsNoCost, itemCostCents) {
        val costIsValid = costStatusIsNoCost || itemCostCents != null

        selectedItem != null && itemCount != null && costIsValid
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Column {
            AutoCompleteDropdownField(
                selectedOption = selectedItem,
                options = items.map {
                    Option(it.id, renderItemName(it.data))
                },
                onSelectedChange = { newItem ->
                    selectedItem = newItem
                },
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "Case", // TODO
                onValueChange = {  }, // TODO
                label = { Text("Unit") },
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .onFocusChanged({ state ->
                        if (!state.isFocused) {
                            // Check for error
                        }
                    }),
                singleLine = true,
                isError = false,
                colors = OutlinedTextFieldDefaults.colors(),
            )

            OutlinedTextField(
                value = "Case", // TODO
                onValueChange = {  }, // TODO
                label = { Text("Pack Size") },
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .onFocusChanged({ state ->
                        if (!state.isFocused) {
                            // Check for error
                        }
                    }),
                singleLine = true,
                isError = false,
                colors = OutlinedTextFieldDefaults.colors(),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "800g", // TODO
                    onValueChange = {  }, // TODO
                    label = { Text("Weight") },
                    modifier = Modifier
                        .height(64.dp)
                        .onFocusChanged({ state ->
                            if (!state.isFocused) {
                                // Check for error
                            }
                        }),
                    singleLine = true,
                    isError = false,
                    colors = OutlinedTextFieldDefaults.colors(),
                )
                // TODO: Dropdown selector ?
                Checkbox(
                    // TODO
                    checked = costStatusIsNoCost,
                    onCheckedChange = { costStatusIsNoCost = it }
                )
                Text("Specify exact weight")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CurrencyInputTextField(
                    label = { Text("Unit Cost") },
                    value = itemCostInput,
                    onValueChange = { itemCostInput = it },
                    onFocusLost = {
                        // Simplify the cost if possible
                        itemCostInput = itemCostInput?.toSimplifiedForm()
                    },
                    enabled = !costStatusIsNoCost
                )

                Checkbox(
                    checked = costStatusIsNoCost,
                    onCheckedChange = { costStatusIsNoCost = it }
                )
//                Switch(
//                    checked = costStatusIsNoCost,
//                    onCheckedChange = { costStatusIsNoCost = it }
//                )
                Text("No cost")
            }

            OutlinedTextField(
                value = itemCountInput,
                onValueChange = { itemCountInput = it },
                label = { Text("Count") },
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
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

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    viewModel.dismissEntryModal()
                }
            ) {
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
                        else -> itemCostCents!!
                    }
                    // TODO(ASAP): Handle the new / changed values
                    val entry = Entry(
                        selectedItem!!.id,
                        itemCount!!,
                        TODO("Unit name"),
                        TODO("Unit weight"),
                        null,
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