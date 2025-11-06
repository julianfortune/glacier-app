package com.julianfortune.glacier.data

// TODO: Can this be converted to inline value class ...?
class Percentage {
    val valueInHundredths: Int

    companion object {
        private const val HUNDREDTHS = 100
        private const val MAX = 100 * HUNDREDTHS

        // TODO: Test
        fun splitBetween(count: Int): List<Percentage> {
            require(count in 1..<MAX) { "Count must be in range 0..$MAX" }

            val targetPercentage = MAX / count
            val remainder = MAX % count

            val first = listOf(Percentage(targetPercentage + remainder))
            val rest = (1..count).map { Percentage(targetPercentage) }
            return first + rest
        }

        fun percentageOf(hundredths: Long): Percentage {
            require(hundredths in 0..MAX) { "Hundredths must be in range 0..$MAX" }

            return Percentage(hundredths.toInt())
        }

        fun percentageOf(hundredths: Int): Percentage {
            require(hundredths in 0..MAX) { "Hundredths must be in range 0..$MAX" }

            return Percentage(hundredths)
        }

        /**
         * Only for use when converting from database
         */
        fun unsafeConstructor(hundredths: Int): Percentage {
            return Percentage(hundredths)
        }
    }

    private constructor(percentHundredths: Int) {
        valueInHundredths = percentHundredths
    }

    override fun toString(): String {
        // TODO: Try to do this without floating point conversion
        return "${valueInHundredths/HUNDREDTHS}%"
    }

    // TODO: Set up a result type
    operator fun plus(other: Percentage): Percentage {
        TODO("...")
    }

    fun toHundredths(): Int {
        return valueInHundredths
    }
}


