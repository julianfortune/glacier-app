package com.julianfortune.glacier.ui.common

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.ui.theme.dynamicScrollbarStyle


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Entity> CollectionView(
    title: String,
    entities: List<T>,
    selectedId: Long? = null,
    onClickCreateNew: (() -> Unit)? = null,
    content: @Composable (T, Modifier, Dp) -> Unit,
) {
    Column {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            TopBar(title) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onClickCreateNew != null) {
                        FilledTonalButton(
                            onClick = {
                                onClickCreateNew.invoke()
                            },
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.height(32.dp).pointerHoverIcon(PointerIcon.Hand),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.filledTonalButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                        ) {
                            Text("New")
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (entities.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Empty collection.")
                }
            } else {
                val listScrollState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listScrollState,
                ) {
                    items(entities) { i ->
                        // Use colors to highlight selected item
                        val modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        val elevation = if (i.id == selectedId) 2.0.dp else 0.0.dp

                        content(i, modifier, elevation)
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(2.dp),
                    adapter = rememberScrollbarAdapter(listScrollState),
                    style = dynamicScrollbarStyle(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}
