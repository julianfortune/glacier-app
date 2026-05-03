package com.julianfortune.glacier.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigInteger

val gramsPerOunce = 28.3495
val ouncesPerPound = 16


// TODO:
// - implement `ofPounds`
// - update database to use `centigrams` instead of grams
// - create a weight entry component (maybe just pounds as Float to start)
// - render weights in imperial in the UI

data class Weight(val centigrams: Long) {
    companion object Companion {
        fun ofCentigrams(centigrams: Long): Weight {
            assert(centigrams >= 0) { "`Weight`s must be positive"}

            return Weight(centigrams)
        }

        fun ofCentigrams(centigrams: BigInteger): Weight {
            // NOTE: Sqlite supports at largest 8-byte signed integers
            assert(centigrams <= Long.MAX_VALUE.toBigInteger()) { "`Weight`s must fit in 8 bytes"}

            return ofCentigrams(centigrams.toLong())
        }

        fun ofPounds(pounds: Float): Weight {
            TODO("Implement")
        }

        fun ofImperial(pounds: Int, ounces: Int): Weight {
            assert(pounds >= 0) { "`pounds`s must be positive"}
            assert(ounces >= 0) { "`ounces`s must be positive"}
            assert(ounces < ouncesPerPound) { "`ounces` must be less than $ouncesPerPound"}

            val poundsInOzHundredths = pounds.toBigInteger() * (ouncesPerPound * 100).toBigInteger()
            val totalOzHundredths = poundsInOzHundredths + (ounces * 100).toBigInteger()
            val centigrams = (totalOzHundredths.toBigDecimal().times(gramsPerOunce.toBigDecimal()))

            return ofCentigrams(centigrams.toBigInteger())
        }
    }

    fun toImperial(): Pair<Long, Int> {
        val totalOzHundredths = (this.centigrams.toBigDecimal() / gramsPerOunce.toBigDecimal()).toBigInteger()
        val (pounds, ozHundredths) = divideAndRemainder(totalOzHundredths, (ouncesPerPound * 100).toBigInteger())

        return Pair(pounds.toLong(), ozHundredths.toInt() / 100)
    }
}

fun divideAndRemainder(first: BigInteger, second: BigInteger): Pair<BigInteger, BigInteger> {
    val results = first.divideAndRemainder(second)
    return Pair(results[0], results[1])
}

class UnitConversionTests {
    @ParameterizedTest(name = "{index}: {0}lbs{1}oz")
    @CsvSource(value = [
        "5000, 8",
        "0, 1",
        "100000, 1",
        "5000, 1",
        "2094967295, 15",
    ])
    fun ofImperialRoundTripProducesSame(pounds: Int, ounces: Int) {
        // WHEN
        val weight = Weight.ofImperial(pounds, ounces)
        val result = weight.toImperial()

        // THEN
        assertThat(result.first.toInt()).isEqualTo(pounds)
        assertThat(result.second).isEqualTo(ounces)
}

    @Test
    fun santiyCheck() {
        println(":>>> LONG MAX_VALUE= ${Long.MAX_VALUE}")
        println(":>>> INT MAX_VALUE= ${Int.MAX_VALUE}")

        println(1.toBigDecimal() / 10.toBigDecimal())
        println(1.toBigDecimal().divide(10.toBigDecimal()))
        println(1.toBigDecimal())
    }
}
