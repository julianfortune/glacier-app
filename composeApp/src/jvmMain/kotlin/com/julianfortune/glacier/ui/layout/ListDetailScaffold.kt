package com.julianfortune.glacier.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ListDetailScaffold(
    detailViewModel: ListDetailControllable,
    listWidth: Dp,
    listView: @Composable (selectedId: Long?, onSelect: (id: Long) -> Unit, clearSelection: () -> Unit) -> Unit,
    separator: @Composable (() -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var selectedId by remember { mutableStateOf<Long?>(null) }

    val onSelect = { id: Long -> selectedId = id }
    val clearSelection = { selectedId = null }

    // Push selected ID to the detail view model to update what is shown
    LaunchedEffect(selectedId) {
        detailViewModel.setCurrentId(selectedId)
    }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(modifier = Modifier.width(listWidth)) {
            listView(selectedId, onSelect, clearSelection)
        }

        separator?.invoke()

        if (selectedId == null) {
            emptyContent?.invoke() ?: Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Nothing selected",
                    color = LocalContentColor.current.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            content()
        }
    }
}