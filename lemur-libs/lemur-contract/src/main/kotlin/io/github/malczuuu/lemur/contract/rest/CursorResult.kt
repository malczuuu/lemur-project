package io.github.malczuuu.lemur.contract.rest

import io.github.malczuuu.lemur.contract.TransportResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

/**
 * See [TransportResponse] for explanation of why fields are nullable.
 */
data class CursorResult<T>(

    @field:NotNull
    val content: List<T>?,

    @field:Valid
    val links: Links?,

    @field:PositiveOrZero
    val totalElements: Long?,
) : TransportResponse {
    data class Links(@field:Size(min = 1) val next: String? = null) : TransportResponse
}
