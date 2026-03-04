package io.github.malczuuu.lemur.app.domain.thing;

import io.github.malczuuu.lemur.app.domain.DomainId;
import io.github.malczuuu.lemur.app.domain.DomainObject;
import java.time.Instant;

public class Thing implements DomainObject {

  private final DomainId id;
  private String name;
  private String description;
  private final Instant createdDate;
  private final Instant lastModifiedDate;
  private Long version;

  public Thing(String name, String description) {
    this(name, description, BLANK_INSTANT);
  }

  public Thing(String name, String description, Instant createdDate) {
    this(DomainId.unassigned(), name, description, createdDate, createdDate, 0L);
  }

  public Thing(
      DomainId id,
      String name,
      String description,
      Instant createdDate,
      Instant lastModifiedDate,
      Long version) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.version = version;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
  public DomainId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public Long getVersion() {
    return version;
  }
}
