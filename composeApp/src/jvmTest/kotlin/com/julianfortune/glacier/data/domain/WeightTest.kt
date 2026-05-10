package com.julianfortune.glacier.data.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class WeightTest {

    @ParameterizedTest(name = "{index}: {0}lbs{1}oz")
    @CsvSource(
        value = [
            "5000, 8",
            "0, 1",
            "0, 15.9",
            "0, 0.5",
            "100000, 1",
            "5000, 1",
            "2094967295, 15",
        ]
    )
    fun ofImperialRoundTripProducesSame(pounds: Int, ounces: Float) {
        // WHEN
        val weight = Weight.ofImperial(pounds, ounces)
        val result = weight.toImperial()

        // THEN
        assertThat(result.first.toInt()).isEqualTo(pounds)
        assertThat(result.second).isEqualTo(ounces)
    }

}
