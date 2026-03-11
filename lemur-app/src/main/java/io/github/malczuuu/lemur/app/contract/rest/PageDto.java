package io.github.malczuuu.lemur.app.contract.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PageDto<T>(
    @JsonProperty("content") List<T> content,
    @JsonProperty("page") Long page,
    @JsonProperty("size") Long size,
    @JsonProperty("totalElements") Long totalElements,
    @JsonProperty("totalPages") Long totalPages) {

  public static <T> PageDto<T> empty() {
    return empty(0L, 0L);
  }

  public static <T> PageDto<T> empty(Long page, Long size) {
    return new PageDto<>(List.of(), page, size, 0L, 0L);
  }

  public static <T> PageDto<T> of(List<T> content, Long page, Long size, Long totalElements) {
    return new PageDto<>(content, page, size, totalElements, (totalElements + size - 1) / size);
  }
}
