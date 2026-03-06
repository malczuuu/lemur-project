package io.github.malczuuu.lemur.app.domain;

public interface DomainObject {

  DomainId getId();

  default boolean isNew() {
    return !getId().isAssigned();
  }
}
