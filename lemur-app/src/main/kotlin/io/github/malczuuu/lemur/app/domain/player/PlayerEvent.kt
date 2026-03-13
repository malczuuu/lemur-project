package io.github.malczuuu.lemur.app.domain.player

import io.github.malczuuu.lemur.app.domain.Event

interface PlayerEvent : Event {

    data class PlayerCreated(val playerId: String) : Event

    data class PlayerUpdated(val playerId: String) : Event

    data class PlayerRatingChanged(val playerId: String, val oldRating: Int, val newRating: Int) : Event

    data class PlayerBanned(val playerId: String) : Event

    data class PlayerUnbanned(val playerId: String) : Event
}
