package io.github.malczuuu.lemur.contract.message.player

import io.github.malczuuu.lemur.contract.TransportMessage
import jakarta.validation.constraints.NotNull

/**
 * See [TransportMessage] for explanation of why fields are nullable.
 */
data class PlayerCreatedMessage(@field:NotNull val playerId: String?) : TransportMessage
