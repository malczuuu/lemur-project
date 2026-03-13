package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.Event;

public interface PlayerEvent extends Event {

  String playerId();

  record PlayerBanned(String playerId) implements PlayerEvent {}

  record PlayerCreated(String playerId) implements PlayerEvent {}

  record PlayerRatingChanged(String playerId, int oldRating, int newRating)
      implements PlayerEvent {}

  record PlayerUnbanned(String playerId) implements PlayerEvent {}

  record PlayerUpdated(String playerId) implements PlayerEvent {}
}
