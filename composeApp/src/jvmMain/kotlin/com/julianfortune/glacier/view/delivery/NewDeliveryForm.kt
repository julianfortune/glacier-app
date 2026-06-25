package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.delivery.DeliveryHeadline
import com.julianfortune.glacier.view.data.Option
import com.julianfortune.glacier.view.shared.*
import com.julianfortune.glacier.viewModel.DeliveryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryForm(
    viewModel: DeliveryViewModel,
    title: String,
    submitButtonText: String,
    initialDelivery: DeliveryHeadline? = null,
    onSubmit: (delivery: DeliveryHeadline) -> Unit
) {
    val suppliers by viewModel.allSuppliers.collectAsState()

    val initialReceivedDate = initialDelivery?.receivedDate?.let { LocalDateInput.of(it) } ?: LocalDateInput.ofToday()

    var receivedDateInput by remember { mutableStateOf<LocalDateInput?>(initialReceivedDate) }
    var dateError by remember { mutableStateOf(false) }
    var selectedSupplier by remember { mutableStateOf<Option<Long>?>(null) }
    var feesCentsInput by remember {
        mutableStateOf(initialDelivery?.feesCents?.let {
            CurrencyInput.fromLong(it)
        })
    }
    var taxesCentsInput by remember {
        mutableStateOf(initialDelivery?.taxesCents?.let {
            CurrencyInput.fromLong(it)
        })
    }

    var feesCents by remember { mutableStateOf<Long?>(null) }
    var taxesCents by remember { mutableStateOf<Long?>(null) }

    val isValid = remember(receivedDateInput, selectedSupplier) {
        receivedDateInput is LocalDateInput.Valid && selectedSupplier != null
    }

    // Update the selected supplier once the `suppliers` state populates
    LaunchedEffect(suppliers) {
        selectedSupplier = suppliers.firstOrNull {
            it.id == initialDelivery?.supplierId
        }?.let {
            Option(it.id, it.data.name)
        }
    }

    LaunchedEffect(feesCentsInput) {
        feesCents = feesCentsInput?.toLong()
    }

    LaunchedEffect(taxesCentsInput) {
        taxesCents = taxesCentsInput?.toLong()
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LocalDateInputTextField(
            value = receivedDateInput,
            onValueChange = {
                receivedDateInput = it
                if (it !is LocalDateInput.Invalid) {
                    dateError = false
                }
            },
            onFocusLost = {
                dateError = (receivedDateInput is LocalDateInput.Invalid)
            },
            label = { Text("Received*") },
            isError = dateError,
            modifier = Modifier.fillMaxWidth(),
        )

        // Supplier Field
        AutoCompleteDropdownField(
            selectedOptionId = selectedSupplier,
            options = suppliers.map {
                Option(it.id, it.data.name)
            },
            onSelectedChange = { newSupplier ->
                selectedSupplier = newSupplier
            },
            label = { Text("Supplier*") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyInputTextField(
                value = feesCentsInput,
                onValueChange = { feesCentsInput = it },
                label = { Text("Fees") },
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(8.dp))

            CurrencyInputTextField(
                value = taxesCentsInput,
                onValueChange = { taxesCentsInput = it },
                label = { Text("Taxes") },
                modifier = Modifier.weight(1f),
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = {
                    viewModel.cancelDeliveryAction()
                }
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = isValid,
                onClick = {
                    val delivery = DeliveryHeadline(
                        // This should be defined in order for button to be enabled
                        (receivedDateInput!! as LocalDateInput.Valid).parsed, // TODO(P3): Better error handling / typing ..?
                        selectedSupplier?.id,
                        taxesCents,
                        feesCents
                    )

                    onSubmit(delivery)
                },
            ) {
                Text(submitButtonText)
            }
        }
    }
}