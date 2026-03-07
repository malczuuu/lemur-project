package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.common.model.Content;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.core.PlayerModel;
import io.github.malczuuu.lemur.app.core.PlayerService;
import io.github.malczuuu.lemur.app.core.RegisterPlayerModel;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  public PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Content<PlayerModel> getPlayers() {
    return playerService.getPlayers();
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PlayerModel> getPlayer(@PathVariable("id") String id) {
    return ResponseEntity.ok(playerService.getPlayer(id));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Identity> registerPlayer(
      @RequestBody @Valid RegisterPlayerModel requestBody) {
    Identity responseBody = playerService.registerPlayer(requestBody);
    return ResponseEntity.created(URI.create("/api/v1/players/" + responseBody.id()))
        .body(responseBody);
  }

  @PostMapping(path = "/{id}/ban")
  public ResponseEntity<Void> banPlayer(@PathVariable("id") String id) {
    playerService.banPlayer(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(path = "/{id}/ban")
  public ResponseEntity<Void> unbanPlayer(@PathVariable("id") String id) {
    playerService.unbanPlayer(id);
    return ResponseEntity.noContent().build();
  }
}
