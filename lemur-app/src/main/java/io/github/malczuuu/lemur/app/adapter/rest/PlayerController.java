package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.common.Content;
import io.github.malczuuu.lemur.app.common.Identity;
import io.github.malczuuu.lemur.app.core.player.PlayerCommand;
import io.github.malczuuu.lemur.app.core.player.PlayerDetails;
import io.github.malczuuu.lemur.app.core.player.PlayerItem;
import io.github.malczuuu.lemur.app.core.player.PlayerService;
import io.github.malczuuu.lemur.contract.rest.player.CreatePlayerDto;
import io.github.malczuuu.lemur.contract.rest.player.PlayerDto;
import io.github.malczuuu.lemur.contract.rest.player.PlayerItemDto;
import io.github.malczuuu.lemur.contract.rest.player.UpdatePlayerDto;
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
  public ResponseEntity<Content<PlayerItemDto>> getPlayers() {
    Content<PlayerItem> items = playerService.getPlayers();
    return ResponseEntity.ok(items.map(mapper::toPlayerItemDto));
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PlayerDto> getPlayer(@PathVariable String id) {
    PlayerDetails player = playerService.getPlayer(id);
    return ResponseEntity.ok(mapper.toPlayerDto(player));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Identity> createPlayer(@RequestBody @Valid CreatePlayerDto requestBody) {
    PlayerCommand.CreatePlayer command = new PlayerCommand.CreatePlayer(requestBody.name());
    Identity responseBody = playerService.createPlayer(command);
    return ResponseEntity.created(URI.create("/api/v1/players/" + responseBody.id()))
        .body(responseBody);
  }

  @PutMapping(
      path = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updatePlayer(
      @PathVariable String id, @RequestBody @Valid UpdatePlayerDto requestBody) {
    PlayerCommand.UpdatePlayer command =
        new PlayerCommand.UpdatePlayer(id, requestBody.name(), requestBody.version());
    playerService.updatePlayer(command);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(path = "/{id}/ban")
  public ResponseEntity<Void> banPlayer(@PathVariable String id) {
    PlayerCommand.BanPlayer command = new PlayerCommand.BanPlayer(id);
    playerService.banPlayer(command);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(path = "/{id}/ban")
  public ResponseEntity<Void> unbanPlayer(@PathVariable String id) {
    PlayerCommand.UnbanPlayer command = new PlayerCommand.UnbanPlayer(id);
    playerService.unbanPlayer(command);
    return ResponseEntity.noContent().build();
  }
}
