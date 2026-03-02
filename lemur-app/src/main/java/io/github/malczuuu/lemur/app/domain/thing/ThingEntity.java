package io.github.malczuuu.lemur.app.domain.thing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "things")
@SequenceGenerator(
    name = "seq_things_thing_id",
    sequenceName = "seq_things_thing_id",
    allocationSize = 1)
@EntityListeners({AuditingEntityListener.class})
public class ThingEntity implements Persistable<Long> {

  @Id
  @Column(name = "thing_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_things_thing_id")
  private @Nullable Long id;

  @Column(name = "thing_name", length = 256)
  private String name = "";

  @Column(name = "thing_description", length = 2048)
  private String description = "";

  @CreatedDate
  @Column(name = "thing_created_date")
  private Instant createdDate = Instant.now();

  @LastModifiedDate
  @Column(name = "thing_last_modified_date")
  private Instant lastModifiedDate = createdDate;

  @Version
  @Column(name = "thing_version")
  private Long version = 0L;

  protected ThingEntity() {}

  public ThingEntity(String name, String description) {
    this(null, name, description);
  }

  public ThingEntity(@Nullable Long id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  @Override
  public @Nullable Long getId() {
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

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean isNew() {
    return getId() == null;
  }
}
