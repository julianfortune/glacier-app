package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryForm(viewModel: DeliveryViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val suppliers by viewModel.allSuppliers.collectAsState() // TODO(P1): Is this right ?

    var receivedDateInput by remember { mutableStateOf("") }
    var supplierId by remember { mutableStateOf<Long?>(null) }

    val isValid = remember(receivedDateInput, supplierId) {
        // TODO: Parse date input
        supplierId != null
    }

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
            value = receivedDateInput,
            onValueChange = { receivedDateInput = it },
            label = { Text("Received") },
            modifier = Modifier.fillMaxWidth().height(64.dp),
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
                supplierId = newId
            },
            modifier = Modifier.fillMaxWidth()
        )

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
                    val receivedData = LocalDate.now() // TODO: WRONG !
                    val delivery = DeliveryDetail(
                        receivedData,
                        supplierId,
                        null,
                        null,
                        emptyList()
                    )

                    coroutineScope.launch {
                        viewModel.save(delivery)
                        viewModel.dismissNewDelivery()
                    }
                },
                enabled = isValid
            ) {
                Text("Create")
            }
        }
    }
}
