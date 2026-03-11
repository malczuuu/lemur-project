package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.DomainEvent;

public record PlayerUpdated(String playerId) implements DomainEvent {}
