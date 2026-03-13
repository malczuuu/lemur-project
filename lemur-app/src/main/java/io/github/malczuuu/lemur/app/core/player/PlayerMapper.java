package io.github.malczuuu.lemur.app.core.player;

import io.github.malczuuu.lemur.app.domain.player.Player;

class PlayerMapper {

  PlayerItem toPlayerItem(Player player) {
    return new PlayerItem(
        player.getId().getValue(), player.getName(), player.getRating(), player.getStatus());
  }

  PlayerDetails toPlayerDetails(Player player) {
    return new PlayerDetails(
        player.getId().getValue(),
        player.getName(),
        player.getRating(),
        player.getStatus(),
        player.getCreatedDate().orElse(null),
        player.getLastModifiedDate().orElse(null),
        player.getVersion());
  }
}
