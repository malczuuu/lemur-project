package io.github.malczuuu.lemur.app.domain.error

interface ErrorAware {
    val error: ErrorType
    val detail: String?
}
