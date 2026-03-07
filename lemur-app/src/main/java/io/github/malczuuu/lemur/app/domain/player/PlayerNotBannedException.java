package io.github.malczuuu.lemur.app.domain.player;

public class PlayerNotBannedException extends PlayerException {

  public PlayerNotBannedException() {
    super(PlayerError.PLAYER_NOT_BANNED);
  }
}
