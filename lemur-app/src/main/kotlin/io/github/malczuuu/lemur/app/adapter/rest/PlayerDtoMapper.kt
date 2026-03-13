package io.github.malczuuu.lemur.app.adapter.rest

import io.github.malczuuu.lemur.app.core.player.PlayerDetails
import io.github.malczuuu.lemur.app.core.player.PlayerItem
import io.github.malczuuu.lemur.contract.rest.player.PlayerDto
import io.github.malczuuu.lemur.contract.rest.player.PlayerItemDto

internal class PlayerDtoMapper {

    fun toPlayerItemDto(player: PlayerItem): PlayerItemDto = PlayerItemDto(
        id = player.id,
        name = player.name,
        rating = player.rating,
        status = player.status.label,
    )

    fun toPlayerDto(player: PlayerDetails): PlayerDto = PlayerDto(
        id = player.id,
        name = player.name,
        rating = player.rating,
        status = player.status.label,
        createdDate = player.createdDate,
        lastModifiedDate = player.lastModifiedDate,
        version = player.version,
    )
}
