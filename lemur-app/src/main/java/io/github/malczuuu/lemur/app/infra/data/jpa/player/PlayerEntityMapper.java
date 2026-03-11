package io.github.malczuuu.lemur.app.infra.data.jpa.player;

import io.github.malczuuu.lemur.app.common.IdAsLong;
import io.github.malczuuu.lemur.app.domain.DomainId;
import io.github.malczuuu.lemur.app.domain.player.Player;
import io.github.malczuuu.lemur.app.domain.player.PlayerBuilder;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;

class PlayerEntityMapper {

  PlayerEntity toEntity(Player player) {
    PlayerEntity entity = new PlayerEntity();

    if (!player.isNew()) {
      IdAsLong id = IdAsLong.parse(player.getId().getValue());
      entity.setId(id.get());
    }

    entity.setName(player.getName());
    entity.setRating(player.getRating());
    entity.setStatus(player.getStatus().getLabel());
    player.getCreatedDate().ifPresent(entity::setCreatedDate);
    player.getLastModifiedDate().ifPresent(entity::setLastModifiedDate);
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
