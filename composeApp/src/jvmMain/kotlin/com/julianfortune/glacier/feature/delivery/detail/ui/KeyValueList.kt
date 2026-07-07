package com.julianfortune.glacier.feature.delivery.detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.theme.AppPreview


@Composable
fun KeyValueList(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier,
    ) {
        content()
    }
}

@Composable
fun KeyValueItem(
    icon: ImageVector,
    key: String,
    value: String,
    valueFontStyle: FontFamily = FontFamily.Default,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
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
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        SelectionContainer(modifier = Modifier.weight(1f)) {
            Text(
                value,
                textAlign = TextAlign.End,
                fontFamily = valueFontStyle,
            )
        }
    }
}

@Preview
@Composable
fun KeyValueListPreview() {
    AppPreview {
        KeyValueList(
            modifier = Modifier.padding(32.dp)
        ) {
            KeyValueItem(Icons.Outlined.Storefront, "Supplier", "ABC Foods")
            KeyValueItem(Icons.Outlined.Gavel, "Taxes", "$10.00", FontFamily.Monospace)
            KeyValueItem(Icons.Outlined.CreditCard, "Fees", "$15.00", FontFamily.Monospace)
        }
    }
}