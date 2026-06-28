package com.julianfortune.glacier.feature.delivery.editor

import com.julianfortune.glacier.feature.delivery.editor.data.DeliveryEditorState
import com.julianfortune.glacier.ui.common.CurrencyInput
import com.julianfortune.glacier.ui.common.LocalDateInput
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeliveryEditorStateHolder(initialState: DeliveryEditorState = DeliveryEditorState()) {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<DeliveryEditorState> = _state.asStateFlow()

    fun updateSuppliers(suppliers: List<Option<Long>>) {
        _state.update { it.copy(suppliers = suppliers) }
    }

    fun updateReceivedDate(input: LocalDateInput?) {
        updateState {
            it.copy(receivedDate = input)
        }
    }

    fun handleReceivedDateFocusLost() {
        _state.update {
            it.copy(receivedDateError = it.receivedDate !is LocalDateInput.Valid)
        }
    }

    fun updateSelectedSupplier(supplier: Option<Long>?) {
        updateState {
            it.copy(selectedSupplier = supplier)
        }
    }

    fun updateFees(input: CurrencyInput) {
        updateState { it.copy(fees = input) }
    }

    fun updateTaxes(input: CurrencyInput) {
        updateState { it.copy(taxes = input) }
    }

    private fun updateState(update: (DeliveryEditorState) -> DeliveryEditorState) {
        _state.update {
            val updated = update(it)
            updated.copy(validated = validate(updated))
        }
    }

    private fun validate(state: DeliveryEditorState): DeliveryEditorState.Validated? {
        if (state.selectedSupplier != null && state.receivedDate is LocalDateInput.Valid) {
            return DeliveryEditorState.Validated(
                state.selectedSupplier.id,
                state.receivedDate.parsed,
                state.taxes?.toLong(),
                state.fees?.toLong()
            )
        }

        return null
    }

    fun resetUserInputs() {
        // Blow away the user inputs
        _state.update { DeliveryEditorState().copy(suppliers = it.suppliers) }
    }

}