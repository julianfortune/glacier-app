package com.julianfortune.glacier.ui.common.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import kotlinx.coroutines.flow.distinctUntilChanged


/**
 * Heavily inspired by: https://mui.com/material-ui/react-autocomplete/
 */

sealed interface Dynamic<out VALUE> {
    data object Loading : Dynamic<Nothing>
    data class Present<VALUE>(val value: VALUE) : Dynamic<VALUE>
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun <ID> AutocompleteSelectV2(
    value: Dynamic<Option<ID>?>,
    options: Dynamic<List<Option<ID>>>,
    onOptionSelected: (Option<ID>?) -> Unit,
    onQueryChange: ((String) -> Unit)? = null,
    onCreateNewOption: ((String) -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val selectedOptionId: ID? = remember(value) {
        (value as? Dynamic.Present)?.value?.id
    }

    val selectedOptionName: String? = remember(value) {
        (value as? Dynamic.Present)?.value?.title
    }

    val textFieldState = rememberTextFieldState(selectedOptionName ?: "")

    LaunchedEffect(textFieldState) {


        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { textInput ->
                println("textInput=$textInput")
                query = textInput.ifEmpty { null }
                println("query=$textInput")
//                onQueryChange?.invoke(textInput)
//                expanded = true
            }
    }

    fun clear() {
        textFieldState.clearText()
        onOptionSelected(null)
    }

    ExposedDropdownMenuBox(
        expanded = enabled && expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        // TODO(P3): Arrow key presses change menu item focus via `Modifier.onPreviewKeyEvent { ... }`
    ) {

        OutlinedTextField(
            // TODO(P3): Use `TextFieldState` and `setTextAndPlaceCursorAtEnd` when filling from a selected menu option
            state = textFieldState,
            label = { label?.invoke() },
            enabled = enabled,
            lineLimits = TextFieldLineLimits.SingleLine,
//            onValueChange = {
//                println("[AutocompleteSelectV2] onValueChange: $it")
//                query = it
//                onOptionSelected(null)
//                expanded = true
//            },
            placeholder = {
                when (value) {
                    is Dynamic.Loading -> Text("Loading")
                    else -> Text("Type to search")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(),
            trailingIcon = {
                when {
                    value is Dynamic.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Show 'clear' button when there's text or a selection
                    textFieldState.text.isNotEmpty() -> IconButton(
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

                    if (!isFocused && query != null) {
//                        clear()
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
                options !is Dynamic.Present -> NoOptionsMenuItemV2("Loading ...")
                options.value.isEmpty() -> NoOptionsMenuItemV2("No matches found")
                else -> options.value.forEach { option ->
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
                            onOptionSelected(option)
                            query = null
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
fun NoOptionsMenuItemV2(text: String) {
    DropdownMenuItem(
        text = { Text(text) },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        enabled = false,
        colors = MenuDefaults.itemColors(),
        onClick = {},
    )
}
