package com.julianfortune.glacier.feature.delivery.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.AutoCompleteDropdownField
import com.julianfortune.glacier.ui.common.CurrencyInput
import com.julianfortune.glacier.ui.common.CurrencyInputTextField
import com.julianfortune.glacier.ui.common.LocalDateInput
import com.julianfortune.glacier.ui.common.LocalDateInputTextField
import com.julianfortune.glacier.ui.common.data.Option

@Preview
@Composable
fun DeliveriesEditorPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        DeliveryEditor(
            "New Delivery",
            "Create",
            DeliveryEditorState(),
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}

@Composable
fun DeliveryEditor(
    title: String,
    submitButtonText: String,
    state: DeliveryEditorState,
    onReceivedDateChange: (date: LocalDateInput?) -> Unit,
    onReceivedDateFocusLost: () -> Unit,
    onSelectedSupplierChange: (supplier: Option<Long>?) -> Unit,
    onFeesChange: (CurrencyInput) -> Unit,
    onTaxesChange: (CurrencyInput) -> Unit,
    onClickCancel: () -> Unit,
    onClickSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LocalDateInputTextField(
            value = state.receivedDate,
            onValueChange = onReceivedDateChange,
            onFocusLost = onReceivedDateFocusLost,
            label = { Text("Received*") },
            isError = state.receivedDateError,
            modifier = Modifier.fillMaxWidth(),
        )

        // Supplier Field
        AutoCompleteDropdownField(
            selectedOptionId = state.selectedSupplierId,
            options = state.suppliers,
            onSelectedChange = onSelectedSupplierChange,
            label = { Text("Supplier*") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyInputTextField(
                value = state.fees,
                onValueChange = onFeesChange,
                label = { Text("Fees") },
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(8.dp))

            CurrencyInputTextField(
                value = state.taxes,
                onValueChange = onTaxesChange,
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
                onClick = onClickCancel,
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = state.validated != null,
                onClick = onClickSubmit,
            ) {
                Text(submitButtonText)
            }
        }
    }
}
