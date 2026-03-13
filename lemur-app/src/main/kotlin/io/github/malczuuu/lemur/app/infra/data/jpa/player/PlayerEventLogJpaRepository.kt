package io.github.malczuuu.lemur.app.infra.data.jpa.player

import org.springframework.data.jpa.repository.JpaRepository

interface PlayerEventLogJpaRepository : JpaRepository<PlayerEventLogEntity, Long> {

    fun findAllByPlayerId(playerId: Long): List<PlayerEventLogEntity>
}
