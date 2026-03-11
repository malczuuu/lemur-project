package io.github.malczuuu.lemur.app.contract.event.player;

import io.github.malczuuu.lemur.app.contract.event.TransportMessage;

public record PlayerUpdatedEvent(String playerId) implements TransportMessage {}
