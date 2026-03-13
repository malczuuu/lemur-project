package io.github.malczuuu.lemur.contract.rest

import io.github.malczuuu.lemur.contract.TransportResponse

data class ContentResult<T>(val content: List<T>) : TransportResponse
