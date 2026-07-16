package com.julianfortune.glacier.data.domain

data class ReportResult(
    val deliveryCount: Int,
    val entryCount: Int,
    val totalWeight: Weight,
    val totalCostCents: Long,
) {
    companion object {
        fun ofZeroes() = ReportResult(0, 0, Weight(0), 0)
    }
}
