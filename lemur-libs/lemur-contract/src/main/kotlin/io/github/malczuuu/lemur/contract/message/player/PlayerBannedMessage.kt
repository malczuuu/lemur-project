package io.github.malczuuu.lemur.contract.message.player

import io.github.malczuuu.lemur.contract.TransportMessage

data class PlayerBannedMessage(val playerId: String) : TransportMessage
