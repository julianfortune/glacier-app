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
import com.julianfortune.glacier.data.domain.entry.CostStatus
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.CurrencyInputTextField
import com.julianfortune.glacier.view.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryForm(viewModel: DeliveryViewModel, deliveryId: Long) {
    val coroutineScope = rememberCoroutineScope()

    val items by viewModel.allItems.collectAsState()

    var itemId by remember { mutableStateOf<Long?>(null) }
    var itemCountInput by remember { mutableStateOf("") }
    var itemCostInput by remember { mutableStateOf("") }
    var costStatusIsNoCost by remember { mutableStateOf(false) }

    // Parsed and valid values
    var itemCount by remember { mutableStateOf<Long?>(null) }
    var itemCostCents by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(itemCountInput) {
        itemCount = itemCountInput.toLongOrNull()
    }

    // TODO: Make this decimal dollars for humans
    LaunchedEffect(itemCostInput) {
        itemCostCents = itemCostInput.toLongOrNull()
    }

    val isValid = remember(itemId, itemCount, costStatusIsNoCost, itemCostCents) {
        val costIsValid = costStatusIsNoCost || itemCostCents != null

        itemId != null && itemCount != null && costIsValid
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "New Entry",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Row {
            AutoCompleteDropdownField(
                label = { Text("Item") },
                options = items.map {
                    Option(it.id, it.data.name)
                },
                onSelectedChange = { newId ->
                    itemId = newId
                },
                modifier = Modifier
            )

            OutlinedTextField(
                value = itemCountInput,
                onValueChange = { itemCountInput = it },
                label = { Text("Count") },
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
        }

        // TODO: Aggregation

        Text(
            "Cost",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
//            CurrencyInputTextField(
//                valueCents = itemCostInput,
//                onValueChange = { itemCostInput = it },
//            )

            OutlinedTextField(
                value = itemCostInput,
                onValueChange = { itemCostInput = it },
                label = { Text("Cost in Cents") },
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
                enabled = !costStatusIsNoCost
            )

            Text("No cost")
            Switch(
                checked = costStatusIsNoCost,
                onCheckedChange = { costStatusIsNoCost = it }
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
                    viewModel.dismissNewEntry()
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
                    val entry = Entry(
                        itemId!!,
                        itemCount!!,
                        costStatus,
                        costCents,
                        null,
                        null,
                        null,
                    )

                    coroutineScope.launch {
                        viewModel.saveEntry(deliveryId, entry)
                        viewModel.dismissNewEntry()
                    }
                },
            ) {
                Text("Create")
            }
        }
    }
}