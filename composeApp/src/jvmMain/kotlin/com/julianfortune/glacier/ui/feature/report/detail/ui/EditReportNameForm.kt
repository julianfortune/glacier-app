package com.julianfortune.glacier.ui.feature.report.detail.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.input.AutocompleteSelect
import com.julianfortune.glacier.ui.common.input.DropdownSelect
import com.julianfortune.glacier.ui.common.input.LocalDateInput
import com.julianfortune.glacier.ui.common.input.LocalDateInputTextField
import com.julianfortune.glacier.ui.feature.report.detail.data.EditReportCriteriaFormState
import com.julianfortune.glacier.ui.feature.report.detail.data.EditReportNameFormState
import com.julianfortune.glacier.ui.feature.report.detail.data.ReportNameBody
import com.julianfortune.glacier.ui.theme.AppPreview

@Composable
fun EditReportCriteriaForm(
    initialValues: ReportNameBody,
    onCancel: () -> Unit = {},
    onSubmit: (newName: ReportNameBody) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stateHolder = remember {
        EditReportNameStateHolder(initialValues)
    }

    EditReportCriteriaFormUi(
        stateHolder.uiState,
        onNameChange = stateHolder::onNameChanged,
        onCancel = onCancel,
        onSubmit = {
            // `onSubmit` should only be invoked when `validData` is defined
            stateHolder.validData?.let { onSubmit(it) }
        },
        modifier = modifier,
    )
}

@Composable
fun EditReportCriteriaFormUi(
    state: EditReportNameFormState,
    onNameChange: (name: String) -> Unit = {},
    onCancel: () -> Unit = {},
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Edit Name",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = state.name.value,
            onValueChange = onNameChange,
            label = { Text("Name *") },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .onFocusChanged({ state ->
                    if (!state.isFocused) {
                        // Check for error
                    }
                }),
            singleLine = true,
            isError = state.name.isError,
            colors = OutlinedTextFieldDefaults.colors(),
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onCancel,
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = state.isValid,
                onClick = onSubmit,
            ) {
                Text("Save")
            }
        }
    }
}
