package com.julianfortune.glacier.ui.feature.report.detail.data

import com.julianfortune.glacier.data.domain.CostStatus
import java.time.LocalDate

data class ReportCriteriaBody(
    val start: LocalDate,
    val end: LocalDate,
    val itemId: Long? = null,
    val categoryId: Long? = null,
    val costStatus: CostStatus? = null,
    val programId: Long? = null,
    val purchasingAccountId: Long? = null,
    val supplierId: Long? = null,
)
