package com.julianfortune.glacier.ui.coordinator.report.data

import com.julianfortune.glacier.data.domain.BasicReport

sealed interface ReportViewState {
    data object Empty : ReportViewState
    data object Loading : ReportViewState
    data class Viewing(
        val currentReport: BasicReport,
    ) : ReportViewState
}
