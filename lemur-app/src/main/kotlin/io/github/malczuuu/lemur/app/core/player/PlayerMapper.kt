package io.github.malczuuu.lemur.app.core.player

import io.github.malczuuu.lemur.app.domain.player.Player

internal class PlayerMapper {

    fun toPlayerItem(player: Player): PlayerItem =
        PlayerItem(id = player.id.value, name = player.name, rating = player.rating, status = player.status)

    fun toPlayerDetails(player: Player): PlayerDetails = PlayerDetails(
        id = player.id.value,
        name = player.name,
        rating = player.rating,
        status = player.status,
        createdDate = player.createdDate,
        lastModifiedDate = player.lastModifiedDate,
        version = player.version,
    )
}
