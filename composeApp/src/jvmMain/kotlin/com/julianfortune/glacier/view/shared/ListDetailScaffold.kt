package com.julianfortune.glacier.view.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.julianfortune.glacier.viewModel.ControllableEntityViewModel

@Composable
fun ListDetailScaffold(
    detailViewModel: ControllableEntityViewModel,
    listWidth: Dp,
    listView: @Composable (selectedId: Long?, onSelect: (id: Long) -> Unit) -> Unit,
    separator: @Composable (() -> Unit)? = null,
    emptyDetail: @Composable () -> Unit = { Text("Nothing selected") },
    content: @Composable () -> Unit,
) {
    var selectedId by remember { mutableStateOf<Long?>(null) }

    val onSelect = { id: Long -> selectedId = id }

    LaunchedEffect(selectedId) {
        detailViewModel.setCurrentId(selectedId)
    }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(modifier = Modifier.width(listWidth)) {
            listView(selectedId, onSelect)
        }

        separator?.invoke()

        if (selectedId == null) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                emptyDetail()
            }
        } else {
            content()
        }
    }
}