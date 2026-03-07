package io.github.malczuuu.lemur.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerRegisteredEvent(@JsonProperty("playerId") String playerId) {}
