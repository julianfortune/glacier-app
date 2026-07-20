package com.julianfortune.glacier.ui.feature.report.detail.data

import com.julianfortune.glacier.data.domain.BasicReport
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.data.domain.CostStatus
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.data.domain.Program
import com.julianfortune.glacier.data.domain.PurchasingAccount
import com.julianfortune.glacier.data.domain.ReportResult
import com.julianfortune.glacier.data.domain.Supplier
import com.julianfortune.glacier.data.domain.Weight

sealed interface ReportDetailState {

    object Loading : ReportDetailState

    data class Success(
        val basicReportId: Long,
        val name: String,
        val criteria: ReportCriteriaState,
        val results: ReportResultState? = null,
        val editName: String? = null,
        val editCriteria: ReportCriteriaBody? = null,
    ) : ReportDetailState

    object Error : ReportDetailState

}

data class ReportCriteriaState(
    val startDate: String,
    val endDate: String,
    val item: ItemHeadline? = null,
    val category: Category? = null,
    val costStatus: CostStatus? = null,
    val program: Program? = null,
    val purchasingAccount: PurchasingAccount? = null,
    val supplier: Supplier? = null,
)

data class ReportResultState(
    val deliveryCount: Int,
    val entryCount: Int,
    val totalWeight: String,
    val totalCost: String,
)