package io.github.malczuuu.lemur.app.infra.data.jpa.player

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "players")
@SequenceGenerator(
    name = "seq_players_player_id",
    sequenceName = "seq_players_player_id",
    allocationSize = 1,
)
@EntityListeners(AuditingEntityListener::class)
data class PlayerEntity(

    @Id
    @Column(name = "player_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_players_player_id")
    private val _id: Long? = null,

    @Column(name = "player_name", length = 200, nullable = false)
    var name: String = "",

    @Column(name = "player_rating", nullable = false)
    var rating: Int = 0,

    @Column(name = "player_status", nullable = false)
    var status: String = "",

    @CreatedDate
    @Column(name = "player_created_date", nullable = false)
    var createdDate: Instant? = null,

    @LastModifiedDate
    @Column(name = "player_last_modified_date", nullable = false)
    var lastModifiedDate: Instant? = null,

    @Version
    @Column(name = "player_version", nullable = false)
    var version: Long? = null,
) : Persistable<Long> {

    override fun getId(): Long? = _id

    override fun isNew(): Boolean = _id == null
}
