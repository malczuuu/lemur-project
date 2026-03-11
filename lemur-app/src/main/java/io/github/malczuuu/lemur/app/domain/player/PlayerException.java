package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.error.DomainException;

public class PlayerException extends DomainException {

  public PlayerException(PlayerError error) {
    super(error);
  }
}
