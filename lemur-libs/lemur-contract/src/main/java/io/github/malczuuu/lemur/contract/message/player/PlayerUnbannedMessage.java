package io.github.malczuuu.lemur.contract.message.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.malczuuu.lemur.contract.TransportMessage;

public record PlayerUnbannedMessage(String playerId) implements PlayerMessage, TransportMessage {

  @JsonIgnore
  @Override
  public String messageType() {
    return "PlayerUnbanned";
  }
}
