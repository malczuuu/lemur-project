package io.github.malczuuu.lemur.app.domain.error

internal fun buildExceptionMessage(error: ErrorType, detail: String?): String =
    error.type + (detail?.let { ": $it" } ?: "")
