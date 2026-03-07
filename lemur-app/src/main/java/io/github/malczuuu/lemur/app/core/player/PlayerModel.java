package io.github.malczuuu.lemur.app.core.player;

import io.github.malczuuu.lemur.app.domain.player.Player;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import java.time.Instant;

public record PlayerModel(
    String id,
    String name,
    int rating,
    PlayerStatus status,
    Instant createdDate,
    Instant lastModifiedDate,
    Long version) {

  public static PlayerModel from(Player player) {
    return new PlayerModel(
        player.getId().getValue(),
        player.getName(),
        player.getRating(),
        player.getStatus(),
        player.getCreatedDate(),
        player.getLastModifiedDate(),
        player.getVersion());
  }
}
