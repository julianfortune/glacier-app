package com.julianfortune.glacier.codec

import com.julianfortune.glacier.data.domain.entry.CostStatus
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success


object CostStatusCodec : Codec<CostStatus> {

    override fun deserialize(value: String): Result<CostStatus, Throwable> {
        return when (value) {
            "NO_COST" -> Success(CostStatus.NO_COST)
            "PURCHASED" -> Success(CostStatus.PURCHASED)
            else -> Failure(Exception("Unrecognized weightUnits: ${value}"))
        }
    }

    override fun serialize(value: CostStatus): String {
        return when (value) {
            CostStatus.NO_COST -> "NO_COST"
            CostStatus.PURCHASED -> "PURCHASED"
        }
    }
}
