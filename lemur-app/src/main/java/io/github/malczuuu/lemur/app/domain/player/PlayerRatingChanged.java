package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.DomainEvent;

public record PlayerRatingChanged(String playerId, int oldRating, int newRating)
    implements DomainEvent {}
