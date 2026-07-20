package com.julianfortune.glacier.ui.feature.report.detail.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.glacier.ui.common.data.FormFieldState
import com.julianfortune.glacier.ui.feature.report.detail.data.EditReportNameFormState
import com.julianfortune.glacier.ui.feature.report.detail.data.ReportNameBody

class EditReportNameStateHolder(initialValues: ReportNameBody) {

    private var nameInput by mutableStateOf(initialValues.name)

    val validData: ReportNameBody? by derivedStateOf {
        val name = nameInput

        if (name != "") {
            ReportNameBody(name)
        } else {
            null
        }
    }

    val uiState: EditReportNameFormState by derivedStateOf {
        EditReportNameFormState(
            name = FormFieldState(nameInput),
            isValid = validData != null,
        )
    }

    fun onNameChanged(name: String) {
        nameInput = name
    }
}