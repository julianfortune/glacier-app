package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.flow.collect


// TODO: Make scrollable
// TODO: Make larger on the screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryForm(viewModel: DeliveryViewModel) {
    val suppliers by viewModel.allSuppliers.collectAsState() // TODO(P1): Is this right ?

    var receivedDate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "New Delivery",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // TODO(P4): Use a more proper date picker component
        OutlinedTextField(
            value = receivedDate,
            onValueChange = { receivedDate = it },
            label = { Text("Received") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(),
            placeholder = { Text("MM/DD/YYYY") }
        )

        // Supplier Field
        AutoCompleteDropdownField(
            label = { Text("Supplier") },
            options = suppliers.map {
                Option(it.id, it.data.name)
            },
            onSelectedChange = { newId ->
                println("Selected: $newId")
            }
        )

        // TODO: Entries UI ...

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    viewModel.dismissNewDelivery()
                }
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    // Validate and submit the form
                    TODO("Save the new delivery")
                },
                enabled = true // TODO: Validation
            ) {
                Text("Create Delivery")
            }
        }
    }
}
