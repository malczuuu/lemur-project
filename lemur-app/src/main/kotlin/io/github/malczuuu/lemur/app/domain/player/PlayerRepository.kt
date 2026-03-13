package io.github.malczuuu.lemur.app.domain.player

interface PlayerRepository {

    fun findAll(): List<Player>

    fun findById(id: String): Player?

    fun lockById(id: String): Player?

    fun save(player: Player): Player
}
