package com.julianfortune.glacier.data.codec

import dev.forkhandles.result4k.Result

interface Codec<A> {
    fun deserialize(value: String): Result<A, CodecError>
    fun serialize(value: A): String
}
