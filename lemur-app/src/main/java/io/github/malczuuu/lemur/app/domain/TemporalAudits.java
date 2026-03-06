package io.github.malczuuu.lemur.app.domain;

import java.time.Instant;

public final class TemporalAudits {

  public static TemporalAudits unaudited() {
    return UNAUDITED;
  }

  public static TemporalAudits of(Instant createdDate, Instant lastModifiedDate) {
    return new TemporalAudits(createdDate, lastModifiedDate);
  }

  public static final TemporalAudits UNAUDITED =
      new TemporalAudits(Instant.EPOCH, Instant.EPOCH, false);

  private final Instant createdDate;
  private final Instant lastModifiedDate;
  private final boolean audited;

  private TemporalAudits(Instant createdDate, Instant lastModifiedDate) {
    this(createdDate, lastModifiedDate, true);
  }

  private TemporalAudits(Instant createdDate, Instant lastModifiedDate, boolean audited) {
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.audited = audited;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public boolean isAudited() {
    return audited;
  }
}
