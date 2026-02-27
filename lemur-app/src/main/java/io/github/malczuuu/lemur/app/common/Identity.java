package io.github.malczuuu.lemur.app.common;

import java.util.Optional;

public record Identity(String id) {

  public static Optional<Long> safeParseLong(String id) {
    try {
      return Optional.of(Long.parseLong(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
