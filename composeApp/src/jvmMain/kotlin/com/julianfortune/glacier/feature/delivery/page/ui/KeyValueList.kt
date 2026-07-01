package com.julianfortune.glacier.feature.delivery.page.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

data class KeyValueItem(
    val icon: ImageVector,
    val key: String,
    val value: String,
    val valueFontStyle: FontFamily = FontFamily.Default,
)

@Composable
fun KeyValueList(
    items: List<KeyValueItem>,
    modifier: Modifier = Modifier,
) {
    items.forEach { i ->
        val (icon, key, value, valueFontStyle) = i
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                key,
                modifier = Modifier.height(14.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                key,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(value, fontFamily = valueFontStyle)
        }
    }
}