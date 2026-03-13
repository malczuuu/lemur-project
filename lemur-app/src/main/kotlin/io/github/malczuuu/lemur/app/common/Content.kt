package io.github.malczuuu.lemur.app.common

data class Content<T>(val content: List<T> = emptyList()) {

    fun <R> map(mapper: (T) -> R): Content<R> = Content(content = content.map(mapper))
}
