package io.github.malczuuu.lemur.app.adapter.rest

import io.github.malczuuu.lemur.app.core.player.PlayerCommand
import io.github.malczuuu.lemur.app.core.player.PlayerService
import io.github.malczuuu.lemur.contract.rest.ContentResult
import io.github.malczuuu.lemur.contract.rest.IdentityResult
import io.github.malczuuu.lemur.contract.rest.player.CreatePlayerDto
import io.github.malczuuu.lemur.contract.rest.player.PlayerDto
import io.github.malczuuu.lemur.contract.rest.player.PlayerItemDto
import io.github.malczuuu.lemur.contract.rest.player.UpdatePlayerDto
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping(path = ["/api/v1/players"])
class PlayerController(private val playerService: PlayerService) {

    private val mapper = PlayerDtoMapper()

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPlayers(): ResponseEntity<ContentResult<PlayerItemDto>> {
        val items = playerService.getPlayers().map(mapper::toPlayerItemDto)
        return ResponseEntity.ok(ContentResult(content = items.content))
    }

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPlayer(@PathVariable id: String): ResponseEntity<PlayerDto> {
        val player = playerService.getPlayer(id)
        return ResponseEntity.ok(mapper.toPlayerDto(player))
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createPlayer(@RequestBody @Valid requestBody: CreatePlayerDto): ResponseEntity<IdentityResult> {
        val command = PlayerCommand.CreatePlayer(name = requestBody.name)
        val identity = playerService.createPlayer(command)
        val responseBody = IdentityResult(id = identity.id)
        return ResponseEntity.created(URI.create("/api/v1/players/${responseBody.id}")).body(responseBody)
    }

    @PutMapping(
        path = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updatePlayer(
        @PathVariable id: String,
        @RequestBody @Valid requestBody: UpdatePlayerDto,
    ): ResponseEntity<Void> {
        val command = PlayerCommand.UpdatePlayer(id = id, name = requestBody.name, version = requestBody.version)
        playerService.updatePlayer(command)
        return ResponseEntity.noContent().build()
    }

    @PostMapping(path = ["/{id}/ban"])
    fun banPlayer(@PathVariable id: String): ResponseEntity<Void> {
        val command = PlayerCommand.BanPlayer(id)
        playerService.banPlayer(command)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping(path = ["/{id}/ban"])
    fun unbanPlayer(@PathVariable id: String): ResponseEntity<Void> {
        val command = PlayerCommand.UnbanPlayer(id)
        playerService.unbanPlayer(command)
        return ResponseEntity.noContent().build()
    }
}
