package com.julianfortune.glacier.feature.delivery.headline.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.julianfortune.glacier.feature.delivery.editor.DeliveryEditor
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDelivery(
    onCancel: () -> Unit,
    onSuccess: (newDelivery: Long) -> Unit,
    viewModel: NewDeliveryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val stateHolder = viewModel.stateHolder

    LaunchedEffect(viewModel.deliveryCreated) {
        viewModel.deliveryCreated.collect { event ->
            stateHolder.resetUserInputs()
            onSuccess(event.id)
        }
    }

    DeliveryEditor(
        "New Delivery",
        "Create",
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
        onClickSubmit = { viewModel.createDelivery() }
    )

}