package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportResponse
import jakarta.validation.constraints.NotNull
import kotlin.time.Instant

/**
 * See [TransportResponse] for explanation of why fields are nullable.
 */
data class PlayerDto(
    @field:NotNull val id: String?,
    @field:NotNull val name: String?,
    @field:NotNull val rating: Int?,
    @field:NotNull val status: String?,
    @field:NotNull val createdDate: Instant?,
    @field:NotNull val lastModifiedDate: Instant?,
    @field:NotNull val version: Long?,
) : TransportResponse
