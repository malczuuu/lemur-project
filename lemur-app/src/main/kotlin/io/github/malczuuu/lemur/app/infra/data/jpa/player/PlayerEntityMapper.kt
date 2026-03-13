package io.github.malczuuu.lemur.app.infra.data.jpa.player

import io.github.malczuuu.lemur.app.domain.DomainId
import io.github.malczuuu.lemur.app.domain.player.Player
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus
import org.springframework.stereotype.Component
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@Component
internal class PlayerEntityMapper {

    fun toEntity(player: Player): PlayerEntity = PlayerEntity(
        _id = if (player.id.assigned) player.id.asLong() else null,
        name = player.name,
        rating = player.rating,
        status = player.status.label,
        createdDate = player.createdDate?.toJavaInstant(),
        lastModifiedDate = player.lastModifiedDate?.toJavaInstant(),
        version = player.version,
    )

    fun toPlayer(entity: PlayerEntity): Player = Player(
        id = DomainId(entity.id!!),
        name = entity.name,
        rating = entity.rating,
        status = PlayerStatus.parse(entity.status),
        createdDate = entity.createdDate?.toKotlinInstant(),
        lastModifiedDate = entity.lastModifiedDate?.toKotlinInstant(),
        version = entity.version!!,
    )
}
