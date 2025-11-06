package com.julianfortune.glacier.data.persisted

import com.julianfortune.glacier.data.Percentage

data class ProgramAllocation(
    override val id: Long,
    val percentage: Percentage,
    val program: Program,
) : Persisted
