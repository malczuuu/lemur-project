package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.DomainId;
import io.github.malczuuu.lemur.app.domain.DomainObject;
import java.time.Instant;
import java.util.Optional;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class Player implements DomainObject {

  private final DomainId id;
  private String name;
  private int rating;
  private PlayerStatus status;
  private final @Nullable Instant createdDate;
  private final @Nullable Instant lastModifiedDate;
  private Long version;

  public Player(String name) {
    this(DomainId.unassigned(), name, 0, PlayerStatus.ACTIVE, null, null, 0L);
  }

  Player(
      DomainId id,
      String name,
      int rating,
      PlayerStatus status,
      @Nullable Instant createdDate,
      @Nullable Instant lastModifiedDate,
      Long version) {
    this.id = id;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.name = name;
    this.rating = rating;
    this.status = status;
    this.version = version;
  }

  public void adjustRating(int delta) {
    this.rating = Math.max(0, this.rating + delta);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public void ban() {
    if (status == PlayerStatus.BANNED) {
      throw new PlayerAlreadyBannedException();
    }
    status = PlayerStatus.BANNED;
  }

  public void unban() {
    if (status == PlayerStatus.ACTIVE) {
      throw new PlayerNotBannedException();
    }
    status = PlayerStatus.ACTIVE;
  }

  public Optional<Instant> getCreatedDate() {
    return Optional.ofNullable(createdDate);
  }

  public Optional<Instant> getLastModifiedDate() {
    return Optional.ofNullable(lastModifiedDate);
  }
}
