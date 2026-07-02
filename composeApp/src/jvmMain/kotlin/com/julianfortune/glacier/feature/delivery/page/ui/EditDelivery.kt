package com.julianfortune.glacier.feature.delivery.page.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Delivery
import com.julianfortune.glacier.feature.delivery.editor.DeliveryEditor
import com.julianfortune.glacier.ui.common.CurrencyInput
import com.julianfortune.glacier.ui.common.LocalDateInput
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDelivery(
    delivery: Delivery,
    onCancel: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EditDeliveryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val stateHolder = viewModel.stateHolder

    LaunchedEffect(viewModel.deliveryUpdated) {
        viewModel.deliveryUpdated.collect {
            stateHolder.resetUserInputs()
            onSuccess()
        }
    }

    LaunchedEffect(delivery) {
        stateHolder.updateReceivedDate(LocalDateInput.of(delivery.received))
        stateHolder.updateSelectedSupplier(delivery.supplier?.id)
        delivery.feesCents?.let {
            stateHolder.updateFees(CurrencyInput.fromLong(it))
        }
        delivery.taxesCents?.let {
            stateHolder.updateTaxes(CurrencyInput.fromLong(it))
        }
    }

    DeliveryEditor(
        "Edit Delivery",
        "Save",
        state,
        onReceivedDateChange = { stateHolder.updateReceivedDate(it) },
        onReceivedDateFocusLost = { stateHolder.handleReceivedDateFocusLost() },
        onSelectedSupplierChange = { stateHolder.updateSelectedSupplier(it?.id) },
        onFeesChange = { stateHolder.updateFees(it) },
        onTaxesChange = { stateHolder.updateTaxes(it) },
        onClickCancel = {
            stateHolder.resetUserInputs()
            onCancel()
        },
        onClickSubmit = { viewModel.updateDelivery(deliveryId = delivery.id) },
    )

}