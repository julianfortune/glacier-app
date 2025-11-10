package com.julianfortune.glacier.codec

import dev.forkhandles.result4k.Result

interface Codec<A> {
    fun deserialize(value: String): Result<A, Throwable>
    fun serialize(value: A): String
}
