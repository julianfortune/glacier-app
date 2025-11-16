package com.julianfortune.glacier.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import org.jetbrains.compose.ui.tooling.preview.Preview

data class Option<ID>(
    val id: ID,
    val title: String,
)

// TODO(P2): Implement async fetching...? (w debounce, see: https://stackoverflow.com/a/78908108)
//  For performance probably won't be able to load all `item`s into memory at once ..
//  Probably *can* get away with loading the suppliers statically, but might be easier to do both the same way..
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun <ID> AutoCompleteDropdownField(
    label: @Composable (() -> Unit)? = null,
    options: List<Option<ID>>,
    onSelectedChange: (ID?) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<Option<ID>?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = input,
            label = label,
            onValueChange = {
                input = it
                selectedOption = null
                expanded = true
            },
            colors = OutlinedTextFieldDefaults.colors(),
            trailingIcon = {
                Row {
                    Icon(
                        Icons.Outlined.Clear,
                        null,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable(
                                onClick = {
                                    input = ""
                                }
                            ),
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .onFocusChanged {
                    expanded = it.isFocused
                },
        )

        // TODO(P1): Highlight selected item
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.title, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        input = option.title

                        onSelectedChange(option.id)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

