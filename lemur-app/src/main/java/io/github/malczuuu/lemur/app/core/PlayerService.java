package io.github.malczuuu.lemur.app.core;

import io.github.malczuuu.lemur.app.common.model.Content;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.domain.player.Player;
import io.github.malczuuu.lemur.app.domain.player.PlayerBanned;
import io.github.malczuuu.lemur.app.domain.player.PlayerCreated;
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway;
import io.github.malczuuu.lemur.app.domain.player.PlayerNotFoundException;
import io.github.malczuuu.lemur.app.domain.player.PlayerRepository;
import io.github.malczuuu.lemur.app.domain.player.PlayerUnbanned;
import io.github.malczuuu.lemur.app.domain.player.PlayerUpdated;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerEventGateway playerEventGateway;

  private final PlayerMapper playerMapper = new PlayerMapper();

  public PlayerService(PlayerRepository playerRepository, PlayerEventGateway playerEventGateway) {
    this.playerRepository = playerRepository;
    this.playerEventGateway = playerEventGateway;
  }

  @Transactional(readOnly = true)
  public Content<PlayerModel> getPlayers() {
    List<Player> players = playerRepository.findAll();
    List<PlayerModel> content = players.stream().map(this::toModel).toList();
    return new Content<>(content);
  }

  @Transactional(readOnly = true)
  public PlayerModel getPlayer(String id) {
    return toModel(fetchPlayer(id));
  }

  @Transactional
  public Identity createPlayer(CreatePlayerModel model) {
    Player player = new Player(model.name());
    player = playerRepository.save(player);
    playerEventGateway.publish(new PlayerCreated(player.getId().getValue()));
    return new Identity(player.getId().getValue());
  }

  @Transactional
  public void updatePlayer(String id, UpdatePlayerModel model) {
    Player player = lockPlayer(id);
    player.setName(model.name());
    player.setVersion(model.version());
    playerEventGateway.publish(new PlayerUpdated(player.getId().getValue()));
    playerRepository.save(player);
  }

  @Transactional
  public void banPlayer(String id) {
    Player player = lockPlayer(id);
    player.ban();
    playerRepository.save(player);
    playerEventGateway.publish(new PlayerBanned(player.getId().getValue()));
  }

  @Transactional
  public void unbanPlayer(String id) {
    Player player = lockPlayer(id);
    player.unban();
    playerRepository.save(player);
    playerEventGateway.publish(new PlayerUnbanned(player.getId().getValue()));
  }

  private Player fetchPlayer(String id) {
    return playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
  }

  private Player lockPlayer(String id) {
    return playerRepository.lockById(id).orElseThrow(PlayerNotFoundException::new);
  }

  private PlayerModel toModel(Player player) {
    return playerMapper.toPlayerModel(player);
  }
}
