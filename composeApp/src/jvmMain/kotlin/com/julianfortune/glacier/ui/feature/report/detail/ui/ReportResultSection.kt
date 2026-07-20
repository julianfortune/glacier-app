package com.julianfortune.glacier.ui.feature.report.detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.component.KeyValueItem
import com.julianfortune.glacier.ui.common.component.KeyValueList
import com.julianfortune.glacier.ui.feature.report.detail.data.ReportResultState
import com.julianfortune.glacier.ui.theme.AppPreview


@Composable
fun ReportResultSection(
    state: ReportResultState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Results",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(0.dp, top = 24.dp, 0.dp, bottom = 8.dp)
        )

        Spacer(Modifier.height(12.dp))

        KeyValueList(
            modifier = Modifier.widthIn(max = 360.dp)
        ) {
            KeyValueItem(
                Icons.Default.Scale,
                "Total Weight",
                state.totalWeight,
                FontFamily.Monospace
            )
            KeyValueItem(
                Icons.Default.Money,
                "Total Cost",
                state.totalCost,
                FontFamily.Monospace
            )
        }
    }
}

@Composable
@Preview
fun ReportResultSectionPreview() = AppPreview {
    ReportResultSection(
        ReportResultState(
            2,
            8,
            "80.0",
            "$120.00"
        )
    )
}