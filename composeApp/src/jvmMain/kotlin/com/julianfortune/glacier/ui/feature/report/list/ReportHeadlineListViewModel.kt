package com.julianfortune.glacier.ui.feature.report.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.repository.BasicReportRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ReportHeadlineListViewModel(
    private val basicReportRepository: BasicReportRepository,
    // TODO: Report view coordinator
) : ViewModel() {

    val allReports = basicReportRepository.getAllAsHeadlines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}