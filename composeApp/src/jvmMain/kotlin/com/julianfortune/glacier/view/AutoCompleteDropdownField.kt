package com.julianfortune.glacier.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

data class Option<ID>(
    val id: ID,
    val title: String,
)

// TODO(P2): Implement async fetching...? (w debounce, see: https://stackoverflow.com/a/78908108)
//  For performance probably won't be able to load all `item`s into memory at once ..
//  Probably *can* get away with loading the suppliers statically, but might be easier to do both the same way..
/**
 * Heavily inspired by: https://mui.com/material-ui/react-autocomplete/
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun <ID> AutoCompleteDropdownField(
    label: @Composable (() -> Unit)? = null,
    options: List<Option<ID>>,
    onSelectedChange: (ID?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var input by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<Option<ID>?>(null) }

    val textFieldValue = remember(input, selectedOption) {
        input ?: selectedOption?.title ?: ""
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
        selectedOption = null
        onSelectedChange(null)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = textFieldValue,
            label = label,
            onValueChange = {
                input = it
                selectedOption = null
                expanded = true
            },
            colors = OutlinedTextFieldDefaults.colors(),
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    // TODO(P3): Improve 'clear' icon UX and re-enable
                    // Show 'clear' button when there's text or a selection
                    if (false && textFieldValue.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
                            onClick = { clear() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear selection"
                            )
                        }
                    }
                    // Dropdown toggle icon
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .onFocusChanged {
                    expanded = it.isFocused
                    if (!it.isFocused && input != null) {
                        clear()
                    }
                },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            when {
                filteredOptions.isEmpty() -> NoOptionsMenuItem()
                else -> filteredOptions.map { option ->
                    val isSelected = option.id == selectedOption?.id

                    // Use colors to highlight selected item
                    val backgroundColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else MenuDefaults.containerColor
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
                            selectedOption = option
                            input = null
                            expanded = false

                            onSelectedChange(option.id)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun NoOptionsMenuItem() {
    DropdownMenuItem(
        text = { Text("No matches found") },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        enabled = false,
        colors = MenuDefaults.itemColors(),
        onClick = {},
    )
}
