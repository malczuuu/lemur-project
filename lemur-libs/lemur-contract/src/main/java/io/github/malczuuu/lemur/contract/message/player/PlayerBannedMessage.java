package io.github.malczuuu.lemur.contract.message.player;

import io.github.malczuuu.lemur.contract.TransportMessage;

public record PlayerBannedMessage(String playerId) implements TransportMessage {}
