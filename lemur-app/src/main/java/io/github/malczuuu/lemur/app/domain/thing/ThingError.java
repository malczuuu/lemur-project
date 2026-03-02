package io.github.malczuuu.lemur.app.domain.thing;

import io.github.malczuuu.lemur.app.domain.error.ErrorCategory;
import io.github.malczuuu.lemur.app.domain.error.ErrorType;

public enum ThingError implements ErrorType {
  THING_NOT_FOUND(ErrorCategory.NOT_FOUND),
  ;

  private final ErrorCategory category;

  ThingError(ErrorCategory category) {
    this.category = category;
  }

  @Override
  public ErrorCategory getCategory() {
    return category;
  }

  @Override
  public String getType() {
    return name();
  }
}
