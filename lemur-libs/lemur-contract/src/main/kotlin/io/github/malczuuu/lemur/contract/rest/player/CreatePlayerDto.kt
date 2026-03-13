package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportRequest
import jakarta.validation.constraints.NotBlank

/**
 * See [TransportRequest] for explanation of why fields are nullable.
 */
data class CreatePlayerDto(@field:NotBlank val name: String?) : TransportRequest
