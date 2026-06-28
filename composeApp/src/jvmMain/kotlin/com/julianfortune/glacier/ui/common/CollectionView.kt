package com.julianfortune.glacier.ui.common

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.ui.theme.darkScrollbarStyle


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> CollectionView(
    title: String,
    entities: List<Entity<T>>,
    selectedId: Long?,
    onClickCreateNew: (() -> Unit)? = null,
    content: @Composable (Entity<T>, Modifier, Dp) -> Unit,
) {
    Column {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
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

        HorizontalDivider(thickness = 1.dp)

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

@Composable
fun SearchTextInputField() {
    Box(
        modifier = Modifier
            .height(32.dp) // Match the buttons
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.extraSmall, // Match the buttons
            )
    ) {
        BasicTextField(
            modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp),
            value = "",
            onValueChange = { },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    innerTextField()
                }
            }
        )
    }
}