package io.github.malczuuu.lemur.app.domain.player;

public class PlayerAlreadyBannedException extends PlayerException {

  public PlayerAlreadyBannedException() {
    super(PlayerError.PLAYER_ALREADY_BANNED);
  }
}
