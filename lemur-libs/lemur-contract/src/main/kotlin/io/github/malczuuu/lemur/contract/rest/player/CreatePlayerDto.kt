package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportRequest
import jakarta.validation.constraints.NotBlank

data class CreatePlayerDto(@field:NotBlank val name: String) : TransportRequest
