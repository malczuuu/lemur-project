package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportResponse
import jakarta.validation.constraints.NotNull

/**
 * See [TransportResponse] for explanation of why fields are nullable.
 */
data class PlayerItemDto(

    @field:NotNull
    val id: String?,

    @field:NotNull
    val name: String?,

    @field:NotNull
    val rating: Int?,

    @field:NotNull
    val status: String?,
) : TransportResponse
