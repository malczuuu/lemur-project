package io.github.malczuuu.lemur.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerRatingChangedEvent(
    @JsonProperty("playerId") String playerId,
    @JsonProperty("oldRating") int oldRating,
    @JsonProperty("newRating") int newRating) {}
