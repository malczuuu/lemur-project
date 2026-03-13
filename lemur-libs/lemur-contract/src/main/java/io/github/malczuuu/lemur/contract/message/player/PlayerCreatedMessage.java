package io.github.malczuuu.lemur.contract.message.player;

import io.github.malczuuu.lemur.contract.TransportMessage;

public record PlayerCreatedMessage(String playerId) implements TransportMessage {}
