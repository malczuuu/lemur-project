package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

data class UpdatePlayerDto(

    @field:NotBlank
    val name: String,

    @field:PositiveOrZero
    val version: Long,
) : TransportRequest
