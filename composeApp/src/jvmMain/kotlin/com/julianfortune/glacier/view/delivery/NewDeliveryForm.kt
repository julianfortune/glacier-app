package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.delivery.DeliveryDetail
import com.julianfortune.glacier.view.AutoCompleteDropdownField
import com.julianfortune.glacier.view.CurrencyInput
import com.julianfortune.glacier.view.CurrencyInputTextField
import com.julianfortune.glacier.view.LocalDateInput
import com.julianfortune.glacier.view.LocalDateInputTextField
import com.julianfortune.glacier.view.data.Option
import com.julianfortune.glacier.viewModel.DeliveryViewModel
import kotlinx.coroutines.launch


// TODO(P2): Update this component to handle editing in addition to creating new
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryForm(
    viewModel: DeliveryViewModel,
    title: String,
    submitButtonText: String,
    initialDeliveryDetail: DeliveryDetail? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    val suppliers by viewModel.allSuppliers.collectAsState()

    var receivedDateInput by remember { mutableStateOf<LocalDateInput?>(LocalDateInput.ofToday()) }
    var dateError by remember { mutableStateOf(false) }
    var selectedSupplier by remember { mutableStateOf<Option<Long>?>(null) }
    var feesCentsInput by remember {
        mutableStateOf(initialDeliveryDetail?.feesCents?.let {
            CurrencyInput.fromLong(it)
        })
    }
    var taxesCentsInput by remember {
        mutableStateOf(initialDeliveryDetail?.taxesCents?.let {
            CurrencyInput.fromLong(it)
        })
    }

    var feesCents by remember { mutableStateOf<Long?>(null) }
    var taxesCents by remember { mutableStateOf<Long?>(null) }

    val isValid = remember(receivedDateInput, selectedSupplier) {
        receivedDateInput is LocalDateInput.Valid && selectedSupplier != null
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
            selectedOption = selectedSupplier,
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
                        (receivedDateInput!! as LocalDateInput.Valid).parsed, // TODO(P3): Better error handling / typing ..?
                        selectedSupplier?.id,
                        taxesCents,
                        feesCents,
                        emptyList()
                    )

                    coroutineScope.launch {
                        val newDeliveryId = viewModel.saveDelivery(delivery)
                        viewModel.newDeliveryCreated(newDeliveryId)
                    }
                },
            ) {
                Text(submitButtonText)
            }
        }
    }
}