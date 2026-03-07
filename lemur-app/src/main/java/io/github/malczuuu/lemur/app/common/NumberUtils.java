package io.github.malczuuu.lemur.app.common;

import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

public final class NumberUtils {

  public static Optional<Long> safeParseLong(@Nullable String s) {
    if (!StringUtils.hasLength(s)) {
      return Optional.empty();
    }
    try {
      return Optional.of(Long.parseLong(s));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private NumberUtils() {}
}
