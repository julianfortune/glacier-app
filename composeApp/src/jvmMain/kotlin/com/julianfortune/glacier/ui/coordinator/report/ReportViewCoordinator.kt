package com.julianfortune.glacier.ui.coordinator.report

import com.julianfortune.glacier.ui.coordinator.report.data.ReportViewState
import kotlinx.coroutines.flow.StateFlow

interface ReportViewCoordinator {
    val state: StateFlow<ReportViewState>

    fun view(reportId: Long)

    fun clear()
}
