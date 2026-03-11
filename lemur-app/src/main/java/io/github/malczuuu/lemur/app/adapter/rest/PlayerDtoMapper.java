package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.contract.rest.player.CreatePlayerDto;
import io.github.malczuuu.lemur.app.contract.rest.player.PlayerDto;
import io.github.malczuuu.lemur.app.contract.rest.player.UpdatePlayerDto;
import io.github.malczuuu.lemur.app.core.CreatePlayerModel;
import io.github.malczuuu.lemur.app.core.PlayerModel;
import io.github.malczuuu.lemur.app.core.UpdatePlayerModel;

final class PlayerDtoMapper {

  PlayerDto toPlayerDto(PlayerModel model) {
    return new PlayerDto(
        model.id(),
        model.name(),
        model.rating(),
        model.status().getLabel(),
        model.createdDate(),
        model.lastModifiedDate(),
        model.version());
  }

  CreatePlayerModel toCreatePlayerModel(CreatePlayerDto dto) {
    return new CreatePlayerModel(dto.name());
  }

  public UpdatePlayerModel toUpdatePlayerModel(UpdatePlayerDto dto) {
    return new UpdatePlayerModel(dto.name(), dto.version());
  }
}
