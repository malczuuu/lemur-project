package io.github.malczuuu.lemur.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerBannedEvent(@JsonProperty("playerId") String playerId) {}
