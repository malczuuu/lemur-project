package io.github.malczuuu.lemur.app.contract.event.player;

import io.github.malczuuu.lemur.app.contract.event.TransportMessage;

public record PlayerRatingChangedEvent(String playerId, int oldRating, int newRating)
    implements TransportMessage {}
