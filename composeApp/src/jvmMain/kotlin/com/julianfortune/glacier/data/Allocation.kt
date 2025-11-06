package com.julianfortune.glacier.data

data class Allocation<A>(
    val percentage: Percentage,
    val allocatedTo: A
)