package io.github.malczuuu.lemur.contract.rest

import io.github.malczuuu.lemur.contract.TransportResponse
import jakarta.validation.constraints.NotNull

/**
 * See [TransportResponse] for explanation of why fields are nullable.
 */
data class IdentityResult(@field:NotNull val id: String?) : TransportResponse
