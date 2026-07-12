package com.julianfortune.glacier.ui.feature.entry.table.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.SideSheet

@Composable
fun EntrySideSheet(
    onClose: () -> Unit,
    content: @Composable (close: () -> Unit) -> Unit,
) {
    SideSheet(
        onClose = onClose,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp,
    ) { dismissSheet ->
        Column(
            modifier = Modifier
                .width(640.dp)
                .padding(16.dp),
        ) {
            content(dismissSheet)
        }
    }
}
