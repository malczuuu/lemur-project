package io.github.malczuuu.lemur.app.common.message;

public record PlayerRatingChangedEvent(String playerId, int oldRating, int newRating) {}
