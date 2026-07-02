package com.julianfortune.glacier.feature.item

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.domain.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemForm(
    viewModel: ItemViewModel,
    title: String,
    submitButtonText: String,
    initialItem: Item? = null,
    onSubmit: (item: Item) -> Unit
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }

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

        // TODO(P0): Category drop down

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = {
                    viewModel.cancelItemOperation()
                }
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = isValid,
                onClick = {
                    TODO()
//                    val item = Item(
//                        name,
//                        // TODO(P0): Save category links
//                        emptyList(),
//                    )
//                    onSubmit(item)
                },
            ) {
                Text(submitButtonText)
            }
        }
    }

}