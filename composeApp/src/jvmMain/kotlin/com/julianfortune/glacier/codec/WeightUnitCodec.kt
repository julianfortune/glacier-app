package com.julianfortune.glacier.codec

import com.julianfortune.glacier.data.domain.item.WeightUnit
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success


object WeightUnitCodec : Codec<WeightUnit> {
    override fun deserialize(value: String): Result<WeightUnit, Throwable> {
        return when (value) {
            "OZ" -> Success(WeightUnit.OZ)
            "LB" -> Success(WeightUnit.LB)
            else -> Failure(Exception("Unrecognized weightUnits: ${value}"))
        }
    }

    override fun serialize(value: WeightUnit): String {
        return when (value) {
            WeightUnit.OZ -> "OZ"
            WeightUnit.LB -> "LB"
        }
    }
}
