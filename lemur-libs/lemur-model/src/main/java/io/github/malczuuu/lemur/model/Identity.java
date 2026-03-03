package io.github.malczuuu.lemur.model;

import java.util.Optional;
import java.util.function.Supplier;

public record Identity(String id) {

  public static Long parseLong(String id, Supplier<RuntimeException> exceptionSupplier) {
    return safeParseLong(id).orElseThrow(exceptionSupplier);
  }

  public static Optional<Long> safeParseLong(String id) {
    try {
      return Optional.of(Long.parseLong(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
