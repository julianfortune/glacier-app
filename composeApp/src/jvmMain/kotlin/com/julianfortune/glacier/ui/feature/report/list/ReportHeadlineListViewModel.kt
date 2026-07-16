package com.julianfortune.glacier.ui.feature.report.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.repository.BasicReportRepository
import com.julianfortune.glacier.ui.coordinator.delivery.data.DeliveryViewState
import com.julianfortune.glacier.ui.coordinator.report.ReportViewCoordinator
import com.julianfortune.glacier.ui.coordinator.report.data.ReportViewState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ReportHeadlineListViewModel(
    private val basicReportRepository: BasicReportRepository,
    private val reportViewCoordinator: ReportViewCoordinator,
) : ViewModel() {

    val selectedId = reportViewCoordinator.state.map {
        when (it) {
            ReportViewState.Empty -> null
            ReportViewState.Loading -> null
            is ReportViewState.Viewing -> it.currentReport.id
        }
    }

    val allReports = basicReportRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onSelect(id: Long) {
        reportViewCoordinator.view(id)
    }

}