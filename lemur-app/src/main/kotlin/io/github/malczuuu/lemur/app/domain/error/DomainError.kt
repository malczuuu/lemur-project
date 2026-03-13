package io.github.malczuuu.lemur.app.domain.error

enum class DomainError(override val category: ErrorCategory) : ErrorType {
    INTERNAL_ERROR(ErrorCategory.INTERNAL_ERROR),
    CONCURRENT_MODIFICATION(ErrorCategory.CONFLICT),
    ;

    override val type: String
        get() = name
}
