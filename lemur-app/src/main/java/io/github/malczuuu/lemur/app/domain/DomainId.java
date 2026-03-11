package io.github.malczuuu.lemur.app.domain;

import org.jspecify.annotations.Nullable;

public final class DomainId {

  public static DomainId unassigned() {
    return UNASSIGNED;
  }

  public static DomainId of(@Nullable Long value) {
    if (value == null || value < 0) {
      throw new IllegalArgumentException("value must be non-null and non-negative");
    }
    return new DomainId(String.valueOf(value));
  }

  public static DomainId of(@Nullable String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("value must be non-null and non-blank");
    }
    return new DomainId(value);
  }

  private static final DomainId UNASSIGNED = new DomainId("");

  private final String value;

  private DomainId(String value) {
    this.value = value.strip();
  }

  public String getValue() {
    return value;
  }

  public boolean isAssigned() {
    return !value.isEmpty();
  }

  @Override
  public String toString() {
    return getValue();
  }
}
