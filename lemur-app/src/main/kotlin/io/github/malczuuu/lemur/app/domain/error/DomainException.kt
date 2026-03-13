package io.github.malczuuu.lemur.app.domain.error

open class DomainException(
    override val error: ErrorType,
    override val detail: String? = null,
    cause: Throwable? = null,
) : RuntimeException(buildExceptionMessage(error, detail), cause),
    ErrorAware
