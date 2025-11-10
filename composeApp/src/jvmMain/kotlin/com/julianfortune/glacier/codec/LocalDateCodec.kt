package com.julianfortune.glacier.codec

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


object LocalDateCodec : Codec<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun deserialize(value: String): Result<LocalDate, Throwable> {
        return try {
            Success(LocalDate.parse(value, formatter))
        } catch (e: DateTimeParseException) {
            Failure(e)
        }
    }

    override fun serialize(value: LocalDate): String {
        return value.format(formatter)
    }
}
