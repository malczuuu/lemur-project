package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.common.Audits;
import io.github.malczuuu.lemur.app.domain.DomainId;
import io.github.malczuuu.lemur.app.domain.DomainObject;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player implements DomainObject {

  private final DomainId id;
  private String name;
  private int rating;
  private PlayerStatus status;
  private final Instant createdDate;
  private final Instant lastModifiedDate;
  private Long version;

  public Player(String name) {
    this(
        DomainId.unassigned(),
        name,
        0,
        PlayerStatus.ACTIVE,
        Audits.UNAUDITED,
        Audits.UNAUDITED,
        0L);
  }

  Player(
      DomainId id,
      String name,
      int rating,
      PlayerStatus status,
      Instant createdDate,
      Instant lastModifiedDate,
      Long version) {
    this.id = id;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.name = name;
    this.rating = rating;
    this.status = status;
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

  public void adjustRating(int delta) {
    this.rating = Math.max(0, this.rating + delta);
  }
}
