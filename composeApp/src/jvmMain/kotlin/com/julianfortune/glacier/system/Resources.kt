package com.julianfortune.glacier.system

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object Resources {
    inline fun <reified T> load(mapper: ObjectMapper, fileName: String): T {
        val inputStream = this::class.java.classLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("Configuration file '$fileName' not found in resources.")

        return inputStream.use { mapper.readValue<T>(it) }
    }
}