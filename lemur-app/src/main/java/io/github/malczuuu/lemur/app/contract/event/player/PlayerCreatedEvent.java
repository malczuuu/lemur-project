package io.github.malczuuu.lemur.app.contract.event.player;

import io.github.malczuuu.lemur.app.contract.event.TransportMessage;

public record PlayerCreatedEvent(String playerId) implements TransportMessage {}
