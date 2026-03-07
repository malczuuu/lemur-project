package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.core.player.PlayerModel;
import io.github.malczuuu.lemur.app.core.player.PlayerService;
import io.github.malczuuu.lemur.app.core.player.RegisterPlayerModel;
import io.github.malczuuu.lemur.model.Content;
import io.github.malczuuu.lemur.model.Identity;
import io.github.malczuuu.lemur.model.rest.PlayerDto;
import io.github.malczuuu.lemur.model.rest.RegisterPlayerDto;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/players")
public class PlayerController {

  private final PlayerService playerService;
  private final PlayerMapper mapper = new PlayerMapper();

  public PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Content<PlayerDto> getPlayers() {
    return playerService.getPlayers().map(mapper::toDto);
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PlayerDto> getPlayer(@PathVariable("id") String id) {
    return ResponseEntity.ok(mapper.toDto(playerService.getPlayer(id)));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Identity> registerPlayer(
      @RequestBody @Valid RegisterPlayerDto requestBody) {
    Identity responseBody = playerService.registerPlayer(mapper.toModel(requestBody));
    return ResponseEntity.created(URI.create("/api/v1/players/" + responseBody.id()))
        .body(responseBody);
  }

  @PostMapping(path = "/{id}/ban")
  public ResponseEntity<Void> banPlayer(@PathVariable("id") String id) {
    playerService.banPlayer(id);
    return ResponseEntity.noContent().build();
  }

  private static class PlayerMapper {

    PlayerDto toDto(PlayerModel model) {
      return new PlayerDto(
          model.id(),
          model.name(),
          model.rating(),
          model.status().getLabel(),
          model.createdDate(),
          model.lastModifiedDate(),
          model.version());
    }

    RegisterPlayerModel toModel(RegisterPlayerDto dto) {
      return new RegisterPlayerModel(dto.name());
    }
  }
}
