package com.julianfortune.glacier.core.util

data class UnexpectedFailureException(
    override val message: String?, override val cause: Throwable? = null
): Throwable(message, cause)