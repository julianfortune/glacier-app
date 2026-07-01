package com.julianfortune.glacier.ui.common


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.theme.AppPreview


@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    actionContent: @Composable () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.height(68.dp).padding(horizontal = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            SelectionContainer(
                modifier = Modifier.weight(1f) // Grow to fill all available space to start
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }

            actionContent()
        }

        HorizontalDivider(thickness = 1.dp)
    }
}

@Preview
@Composable
fun TopBarPreview() {
    AppPreview {
        TopBar(
            title = "Title",
            actionContent = {
                Row {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.QuestionMark, "Example")
                    }
                }
            }
        )
    }
}