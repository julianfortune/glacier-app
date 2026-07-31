package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.data.common.Entity
import java.time.LocalDate

data class ReportHeadline(
    override val id: Long,
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
) : Entity
