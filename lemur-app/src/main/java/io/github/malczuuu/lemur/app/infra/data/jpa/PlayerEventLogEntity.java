package io.github.malczuuu.lemur.app.infra.data.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "player_event_log")
@SequenceGenerator(
    name = "seq_player_event_logs_log_id",
    sequenceName = "seq_player_event_logs_log_id",
    allocationSize = 1)
@EntityListeners({AuditingEntityListener.class})
@NullUnmarked
public class PlayerEventLogEntity implements Persistable<@NonNull Long> {

  @Id
  @Column(name = "log_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_player_event_logs_log_id")
  private Long id;

  @Column(name = "player_id")
  private Long playerId;

  @Column(name = "log_event_type", length = 100, nullable = false)
  private String eventType;

  @Column(name = "log_payload", nullable = false)
  private String payload;

  @Column(name = "log_published_date", nullable = false)
  private Instant publishedDate;

  @Column(name = "log_received_date", nullable = false)
  private Instant receivedDate;

  @CreatedDate
  @Column(name = "log_created_date", nullable = false)
  private Instant createdDate;

  public PlayerEventLogEntity() {}

  @Override
  public boolean isNew() {
    return getId() == null;
  }
}
