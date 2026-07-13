package com.julianfortune.glacier.ui.page.report

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.layout.ListDetailScaffold

@Composable
fun ReportsPage() {
    ListDetailScaffold(
        listWidth = 320.dp,
        listView = {
            // TODO ...
            // ReportHeadlineList()
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        // TODO ...
        // ReportDetail()
    }
}
