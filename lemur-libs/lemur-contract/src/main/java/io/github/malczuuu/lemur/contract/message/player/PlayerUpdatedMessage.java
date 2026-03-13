package io.github.malczuuu.lemur.contract.message.player;

import io.github.malczuuu.lemur.contract.TransportMessage;

public record PlayerUpdatedMessage(String playerId) implements TransportMessage {}
