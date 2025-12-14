package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

fun parseDateSafe(input: String): LocalDate? {
    if (input.isBlank()) return null

    return runCatching {
        val formatter = DateTimeFormatter
            .ofPattern("MM/dd/uuuu")
            .withResolverStyle(ResolverStyle.STRICT) // Rejects invalid dates like Feb 30
        LocalDate.parse(input, formatter)
    }.getOrNull()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryForm(viewModel: DeliveryViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val suppliers by viewModel.allSuppliers.collectAsState()

    var receivedDateInput by remember { mutableStateOf("") }
    var parsedDate by remember { mutableStateOf<LocalDate?>(null) }
    var dateError by remember { mutableStateOf(false) }
    var supplierId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(receivedDateInput) {
        parsedDate = parseDateSafe(receivedDateInput)
        println("parsedDate: $parsedDate")
    }

    val isValid = remember(parsedDate, supplierId) {
        parsedDate != null && supplierId != null
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

        OutlinedTextField(
            value = receivedDateInput,
            onValueChange = { receivedDateInput = it },
            label = { Text("Received") },
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .onFocusChanged({ state ->
                    if (!state.isFocused) {
                        dateError = (!receivedDateInput.isBlank() && parsedDate == null)
                    }
                }),
            singleLine = true,
            isError = dateError,
            colors = OutlinedTextFieldDefaults.colors(),
            supportingText = { Text("MM/DD/YYYY") }
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
                enabled = isValid,
                onClick = {
                    val delivery = DeliveryDetail(
                        // This should be defined in order for button to be enabled
                        parsedDate!!, // TODO(P3): Error handling ..?
                        supplierId,
                        null,
                        null,
                        emptyList()
                    )

                    coroutineScope.launch {
                        val newDeliveryId = viewModel.save(delivery)
                        viewModel.newDeliveryCreated(newDeliveryId)
                    }
                },
            ) {
                Text("Create")
            }
        }
    }
}