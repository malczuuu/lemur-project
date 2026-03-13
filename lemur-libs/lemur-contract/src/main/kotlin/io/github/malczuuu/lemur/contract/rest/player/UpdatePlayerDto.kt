package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

/**
 * See [TransportRequest] for explanation of why fields are nullable.
 */
data class UpdatePlayerDto(

    @field:NotBlank
    val name: String?,

    @field:NotNull
    @field:PositiveOrZero
    val version: Long?,
) : TransportRequest
