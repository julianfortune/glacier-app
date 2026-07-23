package com.julianfortune.glacier.data.codec

import com.julianfortune.glacier.data.domain.Weight
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

// TODO: Tests
object WeightListCodec : Codec<List<Weight>> {
    const val SEPARATOR = ", "

    override fun deserialize(value: String): Result<List<Weight>, CodecError> {
        try {
            require(value.startsWith("["))
            require(value.endsWith("]"))

            val elementString = value
                // Remove leading `[`
                .drop(1)
                // Remove trailing `]`
                .dropLast(1)

            if (elementString == "") return Success(emptyList())

            return Success(elementString.split(SEPARATOR).map {
                Weight.ofCentigrams(it.toLong())
            })
        } catch (_: Throwable) {
            return Failure(CodecError.InvalidInput(value))
        }
    }

    override fun serialize(value: List<Weight>): String {
        val elements = value.joinToString(SEPARATOR) { it.centigrams.toString() }
        return "[$elements]"
    }
}