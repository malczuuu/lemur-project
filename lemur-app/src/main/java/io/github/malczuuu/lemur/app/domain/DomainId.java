package io.github.malczuuu.lemur.app.domain;

public final class DomainId {

  public static DomainId unassigned() {
    return UNASSIGNED;
  }

  public static DomainId of(Long value) {
    if (value < 0) {
      throw new IllegalArgumentException("value cannot be negative");
    }
    return new DomainId(String.valueOf(value));
  }

  public static DomainId of(String value) {
    if (value.isBlank()) {
      throw new IllegalArgumentException("value cannot be blank");
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
