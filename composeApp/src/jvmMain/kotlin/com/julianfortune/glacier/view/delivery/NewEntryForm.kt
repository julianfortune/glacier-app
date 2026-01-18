package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.entry.Entry
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryForm(viewModel: DeliveryViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val items by viewModel.allItems.collectAsState()

    var itemId by remember { mutableStateOf<Long?>(null) }
    var itemCountInput by remember { mutableStateOf("") }
    var costStatusIsNoCost by remember { mutableStateOf(false) }

    val isValid = remember(itemId) {
        itemId != null
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
            OutlinedTextField(
                value = itemCountInput,
                onValueChange = { itemCountInput = it },
                label = { Text("Cost") },
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

            Text("No cost")
            Switch(
                checked = costStatusIsNoCost,
                onCheckedChange = { costStatusIsNoCost = it }
            )
        }

        // TODO(P2): Purchasing Account & Program allocations

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
                    val entry = Entry(
                        itemId!!,
                        TODO(),
                        TODO(),
                        TODO(),
                        TODO(),
                        TODO(),
                        TODO(),
                    )

                    coroutineScope.launch {
                        // TODO: Save new entry
                    }
                },
            ) {
                Text("Create")
            }
        }
    }
}