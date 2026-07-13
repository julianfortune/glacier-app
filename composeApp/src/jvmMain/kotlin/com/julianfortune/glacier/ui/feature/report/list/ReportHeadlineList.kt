package com.julianfortune.glacier.ui.feature.report.list


import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.julianfortune.glacier.ui.common.formatLocalDate
import com.julianfortune.glacier.ui.common.layout.Collection
import org.koin.compose.viewmodel.koinViewModel
import java.time.format.FormatStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHeadlineList(
    viewModel: ReportHeadlineListViewModel = koinViewModel(),
) {
    val selectedId = null // TODO: `by viewModel.selectedId.collectAsState(null)`
    val reportHeadlines by viewModel.allReports.collectAsState(emptyList())

    Collection(
        "Reports",
        reportHeadlines,
        selectedId,
        content = { report, modifier, elevation ->
            val startString = formatLocalDate(report.start, FormatStyle.SHORT)
            val endString = formatLocalDate(report.end, FormatStyle.SHORT)

            // Prevent re-draws when inserting a new report at the top
            key(report.id) {
                ListItem(
                    headlineContent = {
                        Text(report.name)
                    },
                    supportingContent = {
                        Text("$startString to $endString")
                    },
                    modifier = modifier.clickable(
                        enabled = true,
                        onClick = {
                            // TODO ...
                        }
                    ),
                    tonalElevation = elevation,
                )
            }
        },
        onClickCreateNew = {
            // TODO...
        }
    )
}