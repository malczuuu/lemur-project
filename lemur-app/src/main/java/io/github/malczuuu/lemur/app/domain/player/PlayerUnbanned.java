package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.DomainEvent;

public record PlayerUnbanned(String playerId) implements DomainEvent {}
