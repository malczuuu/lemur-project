package io.github.malczuuu.lemur.app.core.player

import io.github.malczuuu.lemur.app.common.Content
import io.github.malczuuu.lemur.app.common.Identity
import io.github.malczuuu.lemur.app.domain.player.Player
import io.github.malczuuu.lemur.app.domain.player.PlayerEvent
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway
import io.github.malczuuu.lemur.app.domain.player.PlayerNotFoundException
import io.github.malczuuu.lemur.app.domain.player.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val playerEventGateway: PlayerEventGateway,
) {
    private val playerMapper = PlayerMapper()

    @Transactional(readOnly = true)
    fun getPlayers(): Content<PlayerItem> {
        val players = playerRepository.findAll()
        val content = players.map { playerMapper.toPlayerItem(it) }
        return Content(content)
    }

    @Transactional(readOnly = true)
    fun getPlayer(id: String): PlayerDetails {
        val player = playerRepository.findById(id) ?: throw PlayerNotFoundException()
        return playerMapper.toPlayerDetails(player)
    }

    @Transactional
    fun createPlayer(command: PlayerCommand.CreatePlayer): Identity {
        var player = Player(name = command.name)
        player = playerRepository.save(player)
        playerEventGateway.publish(PlayerEvent.PlayerCreated(player.id.value))
        return Identity(player.id.value)
    }

    @Transactional
    fun updatePlayer(command: PlayerCommand.UpdatePlayer) {
        val player = playerRepository.lockById(command.id) ?: throw PlayerNotFoundException()
        player.name = command.name
        player.version = command.version
        playerEventGateway.publish(PlayerEvent.PlayerUpdated(player.id.value))
        playerRepository.save(player)
    }

    @Transactional
    fun banPlayer(command: PlayerCommand.BanPlayer) {
        val player = playerRepository.lockById(command.id) ?: throw PlayerNotFoundException()
        player.ban()
        playerRepository.save(player)
        playerEventGateway.publish(PlayerEvent.PlayerBanned(player.id.value))
    }

    @Transactional
    fun unbanPlayer(command: PlayerCommand.UnbanPlayer) {
        val player = playerRepository.lockById(command.id) ?: throw PlayerNotFoundException()
        player.unban()
        playerRepository.save(player)
        playerEventGateway.publish(PlayerEvent.PlayerUnbanned(player.id.value))
    }
}
