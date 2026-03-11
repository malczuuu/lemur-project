package io.github.malczuuu.lemur.app.common.model;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Content<T>(List<T> content) {

  public Content {
    content = List.copyOf(content);
  }

  @Override
  public List<T> content() {
    return Collections.unmodifiableList(content);
  }

  public <R> Content<R> map(Function<? super T, ? extends R> mapper) {
    return new Content<>(content.stream().map(mapper).collect(Collectors.toList()));
  }
}
