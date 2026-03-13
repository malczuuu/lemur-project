package io.github.malczuuu.lemur.app.domain.player

interface PlayerEventGateway {

    fun publish(event: PlayerEvent.PlayerCreated)

    fun publish(event: PlayerEvent.PlayerUpdated)

    fun publish(event: PlayerEvent.PlayerBanned)

    fun publish(event: PlayerEvent.PlayerUnbanned)

    fun publish(event: PlayerEvent.PlayerRatingChanged)
}
