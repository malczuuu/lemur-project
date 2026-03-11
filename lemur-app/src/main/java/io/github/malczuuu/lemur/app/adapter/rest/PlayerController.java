package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.common.model.Content;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.contract.rest.player.CreatePlayerDto;
import io.github.malczuuu.lemur.app.contract.rest.player.PlayerDto;
import io.github.malczuuu.lemur.app.contract.rest.player.UpdatePlayerDto;
import io.github.malczuuu.lemur.app.core.CreatePlayerModel;
import io.github.malczuuu.lemur.app.core.PlayerModel;
import io.github.malczuuu.lemur.app.core.PlayerService;
import io.github.malczuuu.lemur.app.core.UpdatePlayerModel;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/players")
public class PlayerController {

  private final PlayerService playerService;

  private final PlayerDtoMapper mapper = new PlayerDtoMapper();

  public PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Content<PlayerDto> getPlayers() {
    Content<PlayerModel> players = playerService.getPlayers();
    return players.map(mapper::toPlayerDto);
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PlayerDto> getPlayer(@PathVariable String id) {
    PlayerModel player = playerService.getPlayer(id);
    PlayerDto responseBody = mapper.toPlayerDto(player);
    return ResponseEntity.ok(responseBody);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Identity> createPlayer(@RequestBody @Valid CreatePlayerDto requestBody) {
    CreatePlayerModel model = mapper.toCreatePlayerModel(requestBody);
    Identity responseBody = playerService.createPlayer(model);
    return ResponseEntity.created(URI.create("/api/v1/players/" + responseBody.id()))
        .body(responseBody);
  }

  @PutMapping(
      path = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updatePlayer(
      @PathVariable String id, @RequestBody @Valid UpdatePlayerDto requestBody) {
    UpdatePlayerModel model = mapper.toUpdatePlayerModel(requestBody);
    playerService.updatePlayer(id, model);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(path = "/{id}/ban")
  public ResponseEntity<Void> banPlayer(@PathVariable String id) {
    playerService.banPlayer(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(path = "/{id}/ban")
  public ResponseEntity<Void> unbanPlayer(@PathVariable String id) {
    playerService.unbanPlayer(id);
    return ResponseEntity.noContent().build();
  }
}
