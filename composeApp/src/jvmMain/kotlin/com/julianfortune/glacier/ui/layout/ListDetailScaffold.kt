package com.julianfortune.glacier.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.julianfortune.glacier.ui.theme.AppPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ListDetailUiState(
    val selectedId: Long?
)

class ListDetailStateHolder(initialState: ListDetailUiState = ListDetailUiState(null)) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<ListDetailUiState> = _state.asStateFlow()

    fun onSelect(id: Long) {
        _state.value = ListDetailUiState(selectedId = id)
    }

    fun onClearSelection() {
        _state.value = ListDetailUiState(selectedId = null)
    }
}

@Composable
fun ListDetailScaffold(
    listWidth: Dp,
    listView: @Composable (selectedId: Long?, onSelect: (id: Long) -> Unit, clearSelection: () -> Unit) -> Unit,
    separator: @Composable (() -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable (selectedId: Long) -> Unit,
) {
    val stateHolder = remember { ListDetailStateHolder() }
    val uiState = stateHolder.state.collectAsStateWithLifecycle()

    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = modifier) {
        // Left-hand 'list' of items
        Column(modifier = Modifier.width(listWidth)) {
            listView(
                uiState.value.selectedId,
                { stateHolder.onSelect(it) },
                { stateHolder.onClearSelection() },
            )
        }

        // Separator between columns
        separator?.invoke()

        // Right-hand 'detail' view
        uiState.value.selectedId?.let { id ->
            content(id)
        } ?: emptyContent?.invoke() ?: DefaultEmptyContent()
    }
}

@Composable
fun DefaultEmptyContent() {
    Column(
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
}

@Preview
@Composable
fun DefaultPreview() {
    AppPreview {
        ListDetailScaffold(
            listWidth = 200.dp,
            listView = { _, _, _ ->
                Column {
                    ListItem({ Text("Item 1") })
                    ListItem({ Text("Item 2") })
                    ListItem({ Text("Item 3") })
                }
            },
            separator = { VerticalDivider(thickness = 1.dp) },
            content = { selectedId ->
                Text("Selected ID: $selectedId")
            },
        )
    }
}
