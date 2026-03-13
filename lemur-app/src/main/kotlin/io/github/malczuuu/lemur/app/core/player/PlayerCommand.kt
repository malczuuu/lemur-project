package io.github.malczuuu.lemur.app.core.player

import io.github.malczuuu.lemur.app.core.Command

interface PlayerCommand : Command {

    data class CreatePlayer(val name: String) : PlayerCommand

    data class UpdatePlayer(val id: String, val name: String, val version: Long) : PlayerCommand

    data class BanPlayer(val id: String) : PlayerCommand

    data class UnbanPlayer(val id: String) : PlayerCommand
}
