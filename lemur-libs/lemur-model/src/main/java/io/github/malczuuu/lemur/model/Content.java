package io.github.malczuuu.lemur.model;

import java.util.Collections;
import java.util.List;

public record Content<T>(List<T> content) {

  public Content {
    content = List.copyOf(content);
  }

  @Override
  public List<T> content() {
    return Collections.unmodifiableList(content);
  }
}
