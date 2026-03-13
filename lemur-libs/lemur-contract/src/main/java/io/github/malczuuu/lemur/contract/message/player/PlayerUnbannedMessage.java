package io.github.malczuuu.lemur.contract.message.player;

import io.github.malczuuu.lemur.contract.TransportMessage;

public record PlayerUnbannedMessage(String playerId) implements TransportMessage {}
