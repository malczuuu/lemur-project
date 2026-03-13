package io.github.malczuuu.lemur.contract.rest;

import io.github.malczuuu.lemur.contract.TransportResponse;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record CursorResult<T>(List<T> content, @Nullable Links links, @Nullable Long totalElements)
    implements TransportResponse {

  public record Links(@Nullable String next) implements TransportResponse {}
}
