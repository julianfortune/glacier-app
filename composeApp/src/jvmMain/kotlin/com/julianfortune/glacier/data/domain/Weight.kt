package com.julianfortune.glacier.data.domain

import com.julianfortune.glacier.util.dividedBy
import java.math.BigInteger

// TODO(P3): Revisit accuracy at lower and upper magnitude bounds
// TODO(P5): Assess performance (and improve)

data class Weight(val centigrams: Long) {

    companion object Companion {
        const val GRAMS_PER_OZ = 28.3495
        const val OZ_PER_POUND = 16

        fun ofCentigrams(centigrams: Long): Weight {
            assert(centigrams >= 0) { "`Weight`s must be positive" }

            return Weight(centigrams)
        }

        fun ofCentigrams(centigrams: BigInteger): Weight {
            // NOTE: Sqlite supports at largest 8-byte signed integers
            assert(centigrams <= Long.MAX_VALUE.toBigInteger()) { "`Weight`s must fit in 8 bytes" }

            return ofCentigrams(centigrams.toLong())
        }

        fun ofImperial(pounds: Int, ounces: Float): Weight {
            assert(pounds >= 0) { "`pounds`s must be positive" }
            assert(ounces >= 0) { "`ounces`s must be positive" }
            assert(ounces < OZ_PER_POUND) { "`ounces` must be less than $OZ_PER_POUND" }

            val poundsInOzHundredths = pounds.toBigInteger() * (OZ_PER_POUND * 100).toBigInteger()
            val totalOzHundredths = poundsInOzHundredths.toBigDecimal() + (ounces * 100).toBigDecimal()
            val centigrams = (totalOzHundredths.times(GRAMS_PER_OZ.toBigDecimal()))

            return ofCentigrams(centigrams.toBigInteger())
        }
    }

    fun toImperial(): Pair<Long, Float> {
        val totalOzHundredths = (this.centigrams.toBigDecimal() / GRAMS_PER_OZ.toBigDecimal()).toBigInteger()
        val (pounds, ozHundredths) = totalOzHundredths.dividedBy((OZ_PER_POUND * 100).toBigInteger())

        return Pair(pounds.toLong(), (ozHundredths.toFloat() / 100))
    }
}
