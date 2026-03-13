package io.github.malczuuu.lemur.app.domain.error

interface ErrorType {
    val category: ErrorCategory
    val type: String
}
