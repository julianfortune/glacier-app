package com.julianfortune.glacier.data.codec

sealed interface CodecError {
    data class InvalidInput(val input: String): CodecError
}