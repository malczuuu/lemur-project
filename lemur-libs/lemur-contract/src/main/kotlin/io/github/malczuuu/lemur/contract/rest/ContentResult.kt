package io.github.malczuuu.lemur.contract.rest

import io.github.malczuuu.lemur.contract.TransportResponse
import jakarta.validation.constraints.NotNull

/**
 * Fields are deliberately nullable. This way it is possible to first deserialize JSON into object and then validate it
 * using Bean Validation. If fields were non-nullable, deserialization would fail before validation, and there wouldn't
 * be a proper error messages.
 */
data class ContentResult<T>(@field:NotNull val content: List<T>?) : TransportResponse
