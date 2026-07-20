package com.julianfortune.glacier.ui.feature.report.detail.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.ui.common.data.FormFieldState
import com.julianfortune.glacier.ui.common.input.LocalDateInput
import com.julianfortune.glacier.ui.feature.report.detail.data.EditReportCriteriaFormState
import com.julianfortune.glacier.ui.feature.report.detail.data.ReportCriteriaBody

class EditReportCriteriaStateHolder(initialValues: ReportCriteriaBody) {

    private var startDateInput by mutableStateOf<LocalDateInput?>(LocalDateInput.of(initialValues.start))
    private var endDateInput by mutableStateOf<LocalDateInput?>(LocalDateInput.of(initialValues.end))

    private var itemIdInput by mutableStateOf<Long?>(initialValues.itemId)
    private var categoryIdInput by mutableStateOf<Long?>(initialValues.categoryId)

    private var costStatusIsNoCost by mutableStateOf<Boolean?>(initialValues.costStatus?.let { it == CostStatus.NO_COST })

    private var programIdInput by mutableStateOf<Long?>(initialValues.programId)
    private var purchasingAccountIdInput by mutableStateOf<Long?>(initialValues.purchasingAccountId)
    private var supplierIdInput by mutableStateOf<Long?>(initialValues.supplierId)

    val validData: ReportCriteriaBody? by derivedStateOf {
        val startDate = startDateInput
        val endDate = endDateInput

        if (startDate is LocalDateInput.Valid && endDate is LocalDateInput.Valid) {
            ReportCriteriaBody(
                startDate.parsed,
                endDate.parsed,
                itemIdInput,
                categoryIdInput,
                costStatusIsNoCost?.let { isNoCost -> if (isNoCost) CostStatus.NO_COST else CostStatus.PURCHASED },
                programIdInput,
                purchasingAccountIdInput,
                supplierIdInput,
            )
        } else {
            null
        }
    }

    val uiState: EditReportCriteriaFormState by derivedStateOf {
        EditReportCriteriaFormState(
            startDate = FormFieldState(startDateInput?.value ?: ""),
            endDate = FormFieldState(endDateInput?.value ?: ""),
            itemId = FormFieldState(itemIdInput),
            categoryId = FormFieldState(categoryIdInput),
            costStatusIsNoCost = FormFieldState(costStatusIsNoCost),
            programId = FormFieldState(programIdInput),
            purchasingAccountId = FormFieldState(purchasingAccountIdInput),
            supplierId = FormFieldState(supplierIdInput),
            isValid = validData != null,
        )
    }

    fun onStartDateChanged(startDate: LocalDateInput?) {
        startDateInput = startDate
    }

    fun onStartDateFocusLost() {
        // TODO ...
    }

    fun onEndDateChanged(endDate: LocalDateInput?) {
        endDateInput = endDate
    }

    fun onEndDateFocusLost() {
        // TODO ...
    }

    fun onItemIdChanged(itemId: Long?) {
        itemIdInput = itemId
    }

    fun onCategoryIdChanged(categoryId: Long?) {
        categoryIdInput = categoryId
    }

    fun onCostStatusIsNoCostChanged(isNoCost: Boolean?) {
        costStatusIsNoCost = isNoCost
    }

    fun onProgramIdChanged(programId: Long?) {
        programIdInput = programId
    }

    fun onPurchasingAccountIdChanged(purchasingAccountId: Long?) {
        purchasingAccountIdInput = purchasingAccountId
    }

    fun onSupplierIdChanged(supplierId: Long?) {
        supplierIdInput = supplierId
    }
}