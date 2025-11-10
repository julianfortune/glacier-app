package com.julianfortune.glacier.codec

import com.julianfortune.glacier.data.domain.item.WeightUnit
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class WeightUnitCodecTest {

    @ParameterizedTest
    @CsvSource(
        "OZ, OZ",
        "LB, LB"
    )
    fun deserialize(input: String, expectedUnit: WeightUnit) {
        val result = WeightUnitCodec.deserialize(input)

        assertThat(result).isInstanceOf(Success::class.java)
        assertThat(result.valueOrNull()).isEqualTo(expectedUnit)
    }

    @ParameterizedTest
    @CsvSource(
        "OZ, OZ",
        "LB, LB"
    )
    fun serializeWeightUnit(weightUnit: WeightUnit, expectedString: String) {
        val result = WeightUnitCodec.serialize(weightUnit)

        assertThat(result).isEqualTo(expectedString)
    }

    @ParameterizedTest
    @CsvSource(
        "invalid",
        "kg",
        "G",
        "POUND",
        "''"
    )
    @DisplayName("Deserialize invalid input returns Failure")
    fun deserializeInvalidInput(invalidInput: String) {
        val result = WeightUnitCodec.deserialize(invalidInput)

        assertThat(result).isInstanceOf(Failure::class.java)
    }

}