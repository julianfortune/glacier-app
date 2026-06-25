package com.julianfortune.glacier.config

enum class Environment {
    DEVELOPMENT,
    RELEASE,
    ;

    companion object {
        val nameToEntry = Environment.entries.associateBy { it.name }

        fun fromSystem(fallback: Environment): Environment {
            return System.getProperty("env")?.let { nameToEntry.getValue(it) } ?: fallback
        }
    }

}
