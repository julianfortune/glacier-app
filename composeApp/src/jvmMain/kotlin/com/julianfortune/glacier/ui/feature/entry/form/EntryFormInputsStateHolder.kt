package com.julianfortune.glacier.ui.feature.entry.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.ui.common.data.FormFieldState
import com.julianfortune.glacier.ui.common.input.CurrencyInput
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryBody
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryFormEvent
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryFormInputsState
import com.julianfortune.glacier.ui.feature.entry.form.data.EntryFormState
import com.julianfortune.glacier.ui.feature.entry.form.data.ItemWeightState


class EntryFormInputsStateHolder(
    initialEntry: EntryBody? = null,
) {

    private var selectedItemId by mutableStateOf(initialEntry?.itemId)

    // TODO: Mutable state for the item weight dropdown options (and whether it's enabled / disabled)
    // private var itemWeightOptions by mutableStateOf()
    private var itemWeight by mutableStateOf(initialEntry?.itemWeight)

    private var unitWeightPoundsInput by mutableStateOf(
        initialEntry?.unitWeight?.toImperial()?.first?.toString() ?: ""
    )
    private var unitWeightOuncesInput by mutableStateOf(
        initialEntry?.unitWeight?.toImperial()?.second?.toString() ?: ""
    )

    private var costStatusIsNoCost by mutableStateOf(initialEntry?.costStatus == CostStatus.NO_COST)
    private var unitCostInput by mutableStateOf(
        initialEntry?.unitCostCents?.let { CurrencyInput.fromLong(it) }
    )
    private var unitCountInput by mutableStateOf(initialEntry?.unitCount?.toString() ?: "")

    // TODO(!!): Wire together program and purchasing account
    private var selectedProgramId by mutableStateOf<Long?>(initialEntry?.programId)

    private var selectedPurchasingAccountId by mutableStateOf<Long?>(initialEntry?.purchasingAccountId)


    val validData: EntryBody? by derivedStateOf {
        val itemId = selectedItemId
        val pounds = unitWeightPoundsInput.toIntOrNull()
        val ounces = unitWeightOuncesInput.toFloatOrNull() ?: 0f
        val count = unitCountInput.toLongOrNull()

        val costCents = when {
            costStatusIsNoCost -> 0L
            else -> unitCostInput?.toLong() ?: -1L // Marker for invalid/missing paid cost
        }

        val weightIsValid = pounds != null && pounds >= 0 && ounces >= 0f
        val costIsValid = costStatusIsNoCost || costCents >= 0L
        val countIsValid = count != null && count > 0

        if (itemId != null && weightIsValid && costIsValid && countIsValid) {
            val costStatus = if (costStatusIsNoCost) CostStatus.NO_COST else CostStatus.PURCHASED

            EntryBody(
                itemId = itemId,
                unitCount = count,
                unitWeight = Weight.ofImperial(pounds, ounces),
                costStatus = costStatus,
                unitCostCents = costCents,
                itemWeight = null,
                itemsPerUnit = initialEntry?.itemsPerUnit,
                programId = initialEntry?.programId,
                purchasingAccountId = initialEntry?.purchasingAccountId, // Retain existing value if available
            )
        } else {
            null
        }
    }

    val inputsState: EntryFormState by derivedStateOf {
        EntryFormInputsState.Editable(
            selectedItemId = FormFieldState(value = selectedItemId),
            itemWeightState = ItemWeightState.Editable(),
            unitWeight = ,
            unitWeightPoundsInput = FormFieldState(
                value = unitWeightPoundsInput,
                isError = unitWeightPoundsInput.isNotEmpty() && unitWeightPoundsInput.toIntOrNull() == null
            ),
            ounc = FormFieldState(
                value = unitWeightOuncesInput,
                isError = unitWeightOuncesInput.isNotEmpty() && unitWeightOuncesInput.toFloatOrNull() == null
            ),
            costStatusIsNoCost = FormFieldState(value = costStatusIsNoCost),
            unitCost = FormFieldState(value = unitCostInput?.value ?: ""),
            unitCount = FormFieldState(
                value = unitCountInput,
                isError = unitCountInput.isNotEmpty() && unitCountInput.toLongOrNull() == null
            ),
            isValid = validData != null
        )
    }

    fun handleUiEvent(event: EntryFormEvent) {
        when (event) {
            is EntryFormEvent.AccountSelected -> TODO()
            is EntryFormEvent.CostStatusChanged -> costStatusIsNoCost = event.isNoCost
            is EntryFormEvent.ItemCountChanged -> TODO()
            is EntryFormEvent.ItemSelected -> selectedItemId = event.itemId
            is EntryFormEvent.ItemWeightSelected -> TODO()
            is EntryFormEvent.ProgramSelected -> TODO()
            is EntryFormEvent.UnitCostChanged -> unitCostInput = event.value
            is EntryFormEvent.UnitCostLostFocus -> unitCostInput = unitCostInput?.toSimplifiedForm()
            is EntryFormEvent.UnitCountChanged -> unitCountInput = event.value
            is EntryFormEvent.WeightOuncesChanged -> unitWeightOuncesInput = event.value
            is EntryFormEvent.WeightPoundsChanged -> unitWeightPoundsInput = event.value
            else -> {
                // Ignore irrelevant events (submit, abort)
            }
        }
    }
}