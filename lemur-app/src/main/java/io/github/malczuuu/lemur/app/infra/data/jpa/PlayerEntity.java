package io.github.malczuuu.lemur.app.infra.data.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "players")
@SequenceGenerator(
    name = "seq_players_player_id",
    sequenceName = "seq_players_player_id",
    allocationSize = 1)
@EntityListeners({AuditingEntityListener.class})
@NullUnmarked
public class PlayerEntity implements Persistable<@NonNull Long> {

  @Id
  @Column(name = "player_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_players_player_id")
  private Long id;

  @Column(name = "player_name", length = 200, nullable = false)
  private String name;

  @Column(name = "player_rating", nullable = false)
  private int rating;

  @Column(name = "player_status", nullable = false)
  private String status;

  @CreatedDate
  @Column(name = "player_created_date", nullable = false)
  private Instant createdDate;

  @LastModifiedDate
  @Column(name = "player_last_modified_date", nullable = false)
  private Instant lastModifiedDate;

  @Version
  @Column(name = "player_version", nullable = false)
  private Long version;

  public PlayerEntity() {}

  @Override
  public boolean isNew() {
    return getId() == null;
  }
}
