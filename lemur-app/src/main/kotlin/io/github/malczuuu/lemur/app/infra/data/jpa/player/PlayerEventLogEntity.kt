package io.github.malczuuu.lemur.app.infra.data.jpa.player

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "player_event_log")
@SequenceGenerator(
    name = "seq_player_event_logs_log_id",
    sequenceName = "seq_player_event_logs_log_id",
    allocationSize = 1,
)
@EntityListeners(AuditingEntityListener::class)
data class PlayerEventLogEntity(

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_player_event_logs_log_id")
    private val _id: Long? = null,

    @Column(name = "player_id")
    var playerId: Long? = null,

    @Column(name = "log_event_type", length = 100, nullable = false)
    var eventType: String = "",

    @Column(name = "log_payload", nullable = false)
    var payload: String = "",

    @Column(name = "log_published_date", nullable = false)
    var publishedDate: Instant? = null,

    @Column(name = "log_received_date", nullable = false)
    var receivedDate: Instant? = null,

    @CreatedDate
    @Column(name = "log_created_date", nullable = false)
    var createdDate: Instant? = null,
) : Persistable<Long> {

    override fun getId(): Long? = _id

    override fun isNew(): Boolean = _id == null
}
