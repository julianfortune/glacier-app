package com.julianfortune.glacier.ui.common.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option

// TODO(P4): Implement async fetching...? (w debounce, see: https://stackoverflow.com/a/78908108)
//  For performance probably won't be able to load all `item`s into memory at once ..
//  Probably *can* get away with loading the suppliers statically, but might be easier to do both the same way..
/**
 * Heavily inspired by: https://mui.com/material-ui/react-autocomplete/
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun <ID> AutocompleteSelect(
    selectedOptionId: ID?,
    options: List<Option<ID>>,
    onSelectedChange: (Option<ID>?) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var input by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val selectedOptionName: String = remember(selectedOptionId, options) {
        when (selectedOptionId) {
            null -> ""
            else -> options.first { it.id == selectedOptionId }.title
        }
    }

    val textFieldValue = remember(input, selectedOptionName) {
        input ?: selectedOptionName
    }

    val filteredOptions = remember(input, options) {
        if (input != null) {
            options.filter { option ->
                option.title.contains(input!!, ignoreCase = true)
            }
        } else {
            options
        }
    }

    fun clear() {
        input = ""
        onSelectedChange(null)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        // TODO(P3): Arrow key presses change menu item focus via `Modifier.onPreviewKeyEvent { ... }`
    ) {

        OutlinedTextField(
            value = textFieldValue,
            label = label,
            singleLine = true,
            onValueChange = {
                println("[OutlinedTextField] onValueChange: $it")
                input = it
                onSelectedChange(null)
                expanded = true
            },
            colors = OutlinedTextFieldDefaults.colors(),
            trailingIcon = {
                when {
                    // Show 'clear' button when there's text or a selection
                    textFieldValue.isNotEmpty() -> IconButton(
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .focusProperties { canFocus = false },
                        onClick = {
                            clear()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear selection"
                        )
                    }

                    // Otherwise show the toggle icon
                    else -> ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = modifier
                .height(64.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .onFocusChanged {
                    val isFocused = it.hasFocus || it.isFocused

                    if (!isFocused) {
                        expanded = false
                    }

                    if (!isFocused && input != null) {
                        clear()
                    }
                },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 5.dp,
        ) {
            when {
                filteredOptions.isEmpty() -> NoOptionsMenuItem()
                else -> filteredOptions.map { option ->
                    val isSelected = option.id == selectedOptionId

                    // Use colors to highlight selected item
                    val backgroundColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else MaterialTheme.colorScheme.surfaceContainerHigh
                    val textColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else MaterialTheme.colorScheme.onSurface

                    DropdownMenuItem(
                        text = {
                            Text(option.title)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        modifier = Modifier
                            .background(backgroundColor)
                            .pointerHoverIcon(PointerIcon.Hand),
                        colors = MenuDefaults.itemColors().copy(textColor = textColor),
                        onClick = {
                            onSelectedChange(option)
                            input = null
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoOptionsMenuItem() {
    DropdownMenuItem(
        text = { Text("No matches found") },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        enabled = false,
        colors = MenuDefaults.itemColors(),
        onClick = {},
    )
}
