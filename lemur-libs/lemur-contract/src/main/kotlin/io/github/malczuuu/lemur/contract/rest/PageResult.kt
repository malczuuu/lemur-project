package io.github.malczuuu.lemur.contract.rest

import io.github.malczuuu.lemur.contract.TransportResponse
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

/**
 * See [TransportResponse] for explanation of why fields are nullable.
 */
data class PageResult<T>(

    @field:NotNull
    val content: List<T>?,

    @field:PositiveOrZero
    val page: Long?,

    @field:PositiveOrZero
    val size: Long?,

    @field:PositiveOrZero
    val totalElements: Long?,

    @field:PositiveOrZero
    val totalPages: Long?,
) : TransportResponse
