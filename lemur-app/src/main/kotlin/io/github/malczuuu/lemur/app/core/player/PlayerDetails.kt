package io.github.malczuuu.lemur.app.core.player

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus
import kotlin.time.Instant

data class PlayerDetails(
    val id: String,
    val name: String,
    val rating: Int,
    val status: PlayerStatus,
    val createdDate: Instant?,
    val lastModifiedDate: Instant?,
    val version: Long,
)
