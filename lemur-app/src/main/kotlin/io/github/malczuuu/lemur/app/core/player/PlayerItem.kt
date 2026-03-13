package io.github.malczuuu.lemur.app.core.player

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus

data class PlayerItem(val id: String, val name: String, val rating: Int, val status: PlayerStatus)
