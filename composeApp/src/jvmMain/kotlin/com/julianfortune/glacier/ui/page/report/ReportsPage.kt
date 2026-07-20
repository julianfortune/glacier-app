package com.julianfortune.glacier.ui.page.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.layout.ListDetailScaffold
import com.julianfortune.glacier.ui.feature.report.detail.ReportDetail
import com.julianfortune.glacier.ui.feature.report.list.ReportHeadlineList

@Composable
fun ReportsPage() {
    ListDetailScaffold(
        listWidth = 320.dp,
        listView = {
            ReportHeadlineList()
        },
        separator = {
            VerticalDivider(thickness = 1.dp)
        },
    ) {
        ReportDetail()
    }
}
