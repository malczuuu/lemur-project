package io.github.malczuuu.lemur.contract.message.player

import io.github.malczuuu.lemur.contract.TransportMessage

data class PlayerUnbannedMessage(val playerId: String) : TransportMessage
