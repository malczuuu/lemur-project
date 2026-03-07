package io.github.malczuuu.lemur.app.infra.data.jpa;

import io.github.malczuuu.lemur.app.domain.DomainId;
import io.github.malczuuu.lemur.app.domain.player.Player;
import io.github.malczuuu.lemur.app.domain.player.PlayerBuilder;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;

class PlayerEntityMapper {

  PlayerEntity toEntity(Player player) {
    PlayerEntity entity = new PlayerEntity();

    if (!player.isNew()) {
      entity.setId(Long.parseLong(player.getId().getValue()));
    }

    entity.setName(player.getName());
    entity.setRating(player.getRating());
    entity.setStatus(player.getStatus().getLabel());
    entity.setCreatedDate(player.getCreatedDate());
    entity.setLastModifiedDate(player.getLastModifiedDate());
    entity.setVersion(player.getVersion());
    return entity;
  }

  Player toPlayer(PlayerEntity entity) {
    PlayerBuilder builder =
        new PlayerBuilder()
            .withId(DomainId.of(entity.getId()))
            .withName(entity.getName())
            .withRating(entity.getRating())
            .withStatus(PlayerStatus.parse(entity.getStatus()))
            .withCreatedDate(entity.getCreatedDate())
            .withLastModifiedDate(entity.getLastModifiedDate())
            .withVersion(entity.getVersion());
    return builder.build();
  }
}
