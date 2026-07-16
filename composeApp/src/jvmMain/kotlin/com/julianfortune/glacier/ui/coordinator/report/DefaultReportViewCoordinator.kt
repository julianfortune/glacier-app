package com.julianfortune.glacier.ui.coordinator.report

import com.julianfortune.glacier.data.domain.BasicReport
import com.julianfortune.glacier.data.repository.BasicReportRepository
import com.julianfortune.glacier.ui.coordinator.report.data.ReportViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn


@OptIn(ExperimentalCoroutinesApi::class)
class DefaultReportViewCoordinator(
    basicReportRepository: BasicReportRepository,
    coroutineScope: CoroutineScope,
) : ReportViewCoordinator {

    private val _targetBasicReportId = MutableStateFlow<Long?>(null)

    private val _report: Flow<BasicReport?> = _targetBasicReportId.flatMapLatest { id ->
        id?.let { basicReportRepository.getById(it) } ?: flowOf(null)
    }

    override val state = combine(_report, _targetBasicReportId) { report, targetId ->
        when {
            targetId == null -> ReportViewState.Empty
            report != null && report.id == targetId -> ReportViewState.Viewing(report)
            else -> ReportViewState.Loading
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportViewState.Empty,
    )

    override fun view(reportId: Long) {
        _targetBasicReportId.value = reportId
    }

    override fun clear() {
        _targetBasicReportId.value = null
    }

}