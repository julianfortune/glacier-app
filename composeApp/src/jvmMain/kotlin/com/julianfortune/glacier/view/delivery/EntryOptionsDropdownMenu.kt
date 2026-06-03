package com.julianfortune.glacier.view.delivery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@Composable
fun EntryOptionsDropdownMenu(
    edit: () -> Unit,
    delete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .size(28.dp)
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            Icon(
                Icons.Default.MoreVert, contentDescription = "Options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = {
                    edit()
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Delete") },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = {
                    delete()
                    expanded = false
                }
            )
        }
    }
}