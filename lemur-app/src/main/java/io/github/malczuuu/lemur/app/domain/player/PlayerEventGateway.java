package io.github.malczuuu.lemur.app.domain.player;

public interface PlayerEventGateway {

  void publish(PlayerCreated event);

  void publish(PlayerUpdated event);

  void publish(PlayerBanned event);

  void publish(PlayerUnbanned event);

  void publish(PlayerRatingChanged event);
}
