package com.julianfortune.glacier.feature.delivery.page.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import com.julianfortune.glacier.ui.common.TopBar


@Composable
fun DeliveryTopBar(
    title: String,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBar(title, modifier) {
        Row {
            IconButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onClickDelete
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete delivery"
                )
            }
        }
    }
}


@Preview
@Composable
fun DeliveryPageTopBarPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        DeliveryTopBar(title = "Title", onClickDelete = {})
    }
}