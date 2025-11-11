package com.julianfortune.glacier.data.domain.entry

import com.julianfortune.glacier.util.Percentage

data class Allocation<A>(
    val percentage: Percentage,
    val allocatedTo: A
)
