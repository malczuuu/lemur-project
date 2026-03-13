package io.github.malczuuu.lemur.app.infra.data.jpa.player

import io.github.malczuuu.lemur.app.domain.player.Player
import io.github.malczuuu.lemur.app.domain.player.PlayerRepository
import org.springframework.stereotype.Component

@Component
internal class PrimaryPlayerRepository(
    private val playerRepository: PlayerJpaRepository,
    private val mapper: PlayerEntityMapper,
) : PlayerRepository {

    override fun findAll(): List<Player> = playerRepository.findAll().map(mapper::toPlayer)

    override fun findById(id: String): Player? = id.toLongOrNull()
        ?.let { playerRepository.findById(it).orElse(null) }
        ?.let(mapper::toPlayer)

    override fun lockById(id: String): Player? = id.toLongOrNull()
        ?.let { playerRepository.lockById(it).orElse(null) }
        ?.let(mapper::toPlayer)

    override fun save(player: Player): Player {
        var entity = mapper.toEntity(player)
        entity = playerRepository.save(entity)
        return mapper.toPlayer(entity)
    }
}
