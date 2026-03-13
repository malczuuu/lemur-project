package io.github.malczuuu.lemur.app.domain.player;

public interface PlayerEventGateway {

  void publish(PlayerEvent.PlayerCreated event);

  void publish(PlayerEvent.PlayerUpdated event);

  void publish(PlayerEvent.PlayerBanned event);

  void publish(PlayerEvent.PlayerUnbanned event);

  void publish(PlayerEvent.PlayerRatingChanged event);
}
