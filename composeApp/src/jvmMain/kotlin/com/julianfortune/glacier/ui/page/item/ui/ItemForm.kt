package com.julianfortune.glacier.ui.page.item.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.julianfortune.glacier.ui.page.item.data.ItemBody
import com.julianfortune.glacier.ui.theme.AppPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemForm(
    categoryOptions: List<Option<Long>>,
    title: String,
    submitButtonText: String,
    initialValue: ItemBody? = null,
    onCancel: () -> Unit,
    onSubmit: (body: ItemBody) -> Unit,
) {
    var name by remember { mutableStateOf(initialValue?.name ?: "") }
    var categoryId by remember { mutableStateOf(initialValue?.categoryId) }

    val isValid = remember(name) {
        name != ""
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

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
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
            isError = false,
            colors = OutlinedTextFieldDefaults.colors(),
        )

        AutocompleteSelect(
            categoryId,
            categoryOptions,
            {
                categoryId = it?.id
            },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onCancel
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = isValid,
                onClick = {
                    onSubmit(ItemBody(name, categoryId))
                },
            ) {
                Text(submitButtonText)
            }
        }
    }

}

@Preview
@Composable
fun ItemFormPreview() = AppPreview {
    ItemForm(
        categoryOptions = emptyList(),
        title = "Title",
        submitButtonText = "Save",
        initialValue = null,
        onCancel = {},
        onSubmit = {},
    )
}