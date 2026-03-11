package io.github.malczuuu.lemur.app.domain.player;

public class PlayerNotFoundException extends PlayerException {

  public PlayerNotFoundException() {
    super(PlayerError.PLAYER_NOT_FOUND);
  }
}
