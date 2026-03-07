package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.error.ErrorCategory;
import io.github.malczuuu.lemur.app.domain.error.ErrorType;

public enum PlayerError implements ErrorType {
  PLAYER_NOT_FOUND(ErrorCategory.NOT_FOUND),
  PLAYER_ALREADY_BANNED(ErrorCategory.CONFLICT),
  PLAYER_NOT_BANNED(ErrorCategory.CONFLICT),
  ;

  private final ErrorCategory category;

  PlayerError(ErrorCategory category) {
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
