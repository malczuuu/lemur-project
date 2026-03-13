package io.github.malczuuu.lemur.contract.message.player;

import io.github.malczuuu.lemur.contract.TransportMessage;

public record PlayerRatingChangedMessage(String playerId, int oldRating, int newRating)
    implements TransportMessage {}
