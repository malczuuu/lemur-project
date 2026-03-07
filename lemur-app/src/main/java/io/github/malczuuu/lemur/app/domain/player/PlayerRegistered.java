package io.github.malczuuu.lemur.app.domain.player;

import io.github.malczuuu.lemur.app.domain.DomainEvent;

public record PlayerRegistered(String playerId) implements DomainEvent {}
