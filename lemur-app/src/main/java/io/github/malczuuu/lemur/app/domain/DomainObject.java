package io.github.malczuuu.lemur.app.domain;

import java.time.Instant;

public interface DomainObject {

  Instant BLANK_INSTANT = Instant.EPOCH;

  DomainId getId();

  default boolean isNew() {
    return !getId().isAssigned();
  }
}
