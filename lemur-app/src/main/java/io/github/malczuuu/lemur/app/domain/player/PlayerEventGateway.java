package io.github.malczuuu.lemur.app.domain.player;

public interface PlayerEventGateway {

  void publish(PlayerRegistered event);

  void publish(PlayerBanned event);

  void publish(PlayerUnbanned event);

  void publish(PlayerRatingChanged event);
}
