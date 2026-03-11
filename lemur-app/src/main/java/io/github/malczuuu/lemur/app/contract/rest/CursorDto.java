package io.github.malczuuu.lemur.app.contract.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record CursorDto<T>(
    @JsonProperty("content") List<T> content,
    @JsonProperty("links") LinksDto links,
    @Nullable Long totalElements) {

  public static <T> CursorDto<T> empty() {
    return new CursorDto<>(List.of(), LinksDto.empty(), null);
  }

  public static <T> CursorDto<T> of(List<T> content) {
    return new CursorDto<>(content, new LinksDto(null), null);
  }

  public static <T> CursorDto<T> of(List<T> content, @Nullable String next) {
    return new CursorDto<>(content, new LinksDto(next), null);
  }

  public static <T> CursorDto<T> of(
      List<T> content, @Nullable String next, @Nullable Long totalElements) {
    return new CursorDto<>(content, new LinksDto(next), totalElements);
  }

  public record LinksDto(@JsonProperty("next") @Nullable String next) {

    public static LinksDto empty() {
      return new LinksDto(null);
    }
  }
}
