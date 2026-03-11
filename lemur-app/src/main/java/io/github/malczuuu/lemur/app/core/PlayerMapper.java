package io.github.malczuuu.lemur.app.core;

import io.github.malczuuu.lemur.app.domain.player.Player;

class PlayerMapper {

  PlayerModel toPlayerModel(Player player) {
    return new PlayerModel(
        player.getId().getValue(),
        player.getName(),
        player.getRating(),
        player.getStatus(),
        player.getCreatedDate().orElse(null),
        player.getLastModifiedDate().orElse(null),
        player.getVersion());
  }
}
