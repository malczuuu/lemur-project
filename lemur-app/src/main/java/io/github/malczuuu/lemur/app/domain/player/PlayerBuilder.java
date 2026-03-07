package io.github.malczuuu.lemur.app.domain.player;

import static java.util.Objects.requireNonNull;

import io.github.malczuuu.lemur.app.common.Audits;
import io.github.malczuuu.lemur.app.domain.DomainId;
import java.time.Instant;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

public class PlayerBuilder {

  private @Nullable DomainId id;
  private @Nullable String name;
  private int rating;
  private @Nullable PlayerStatus status;
  private @Nullable Instant createdDate;
  private @Nullable Instant lastModifiedDate;
  private @Nullable Long version;

  public PlayerBuilder withId(DomainId id) {
    this.id = id;
    return this;
  }

  public PlayerBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public PlayerBuilder withRating(int rating) {
    this.rating = rating;
    return this;
  }

  public PlayerBuilder withStatus(PlayerStatus status) {
    this.status = status;
    return this;
  }

  public PlayerBuilder withCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public PlayerBuilder withLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    return this;
  }

  public PlayerBuilder withVersion(Long version) {
    this.version = version;
    return this;
  }

  public Player build() {
    return new Player(
        Optional.ofNullable(id).orElse(DomainId.unassigned()),
        requireNonNull(name, "name must be non-null"),
        rating,
        Optional.ofNullable(status).orElse(PlayerStatus.ACTIVE),
        Optional.ofNullable(createdDate).orElse(Audits.UNAUDITED),
        Optional.ofNullable(lastModifiedDate).orElse(Audits.UNAUDITED),
        Optional.ofNullable(version).orElse(0L));
  }
}
