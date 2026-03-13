package io.github.malczuuu.lemur.contract.message.player

import io.github.malczuuu.lemur.contract.TransportMessage
import jakarta.validation.constraints.NotNull

/**
 * See [TransportMessage] for explanation of why fields are nullable.
 */
data class PlayerRatingChangedMessage(

    @field:NotNull
    val playerId: String?,

    @field:NotNull
    val oldRating: Int?,

    @field:NotNull
    val newRating: Int?,
) : TransportMessage
