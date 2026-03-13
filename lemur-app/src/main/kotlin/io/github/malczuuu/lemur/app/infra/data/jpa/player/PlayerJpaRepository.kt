package io.github.malczuuu.lemur.app.infra.data.jpa.player

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface PlayerJpaRepository : JpaRepository<PlayerEntity, Long> {

    @Query("select p from PlayerEntity p order by p.createdDate ASC, p._id ASC")
    override fun findAll(): List<PlayerEntity>

    @Query("select p from PlayerEntity p where p._id = :id")
    override fun findById(id: Long): Optional<PlayerEntity>

    @Query("select p from PlayerEntity p where p._id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun lockById(id: Long): Optional<PlayerEntity>

    override fun <T : PlayerEntity> save(entity: T): T
}
