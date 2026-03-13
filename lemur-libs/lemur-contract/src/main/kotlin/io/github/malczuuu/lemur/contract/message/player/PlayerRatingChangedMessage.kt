package io.github.malczuuu.lemur.contract.message.player

import io.github.malczuuu.lemur.contract.TransportMessage

data class PlayerRatingChangedMessage(val playerId: String, val oldRating: Int, val newRating: Int) : TransportMessage
