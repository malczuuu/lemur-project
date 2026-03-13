package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.core.player.PlayerDetails;
import io.github.malczuuu.lemur.app.core.player.PlayerItem;
import io.github.malczuuu.lemur.contract.rest.player.PlayerDto;
import io.github.malczuuu.lemur.contract.rest.player.PlayerItemDto;

final class PlayerDtoMapper {

  PlayerItemDto toPlayerItemDto(PlayerItem player) {
    return new PlayerItemDto(
        player.id(), player.name(), player.rating(), player.status().getLabel());
  }

  PlayerDto toPlayerDto(PlayerDetails model) {
    return new PlayerDto(
        model.id(),
        model.name(),
        model.rating(),
        model.status().getLabel(),
        model.createdDate(),
        model.lastModifiedDate(),
        model.version());
  }
}
