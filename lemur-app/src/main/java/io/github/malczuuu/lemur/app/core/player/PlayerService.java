package io.github.malczuuu.lemur.app.core.player;

import io.github.malczuuu.lemur.app.common.Content;
import io.github.malczuuu.lemur.app.common.Identity;
import io.github.malczuuu.lemur.app.domain.player.Player;
import io.github.malczuuu.lemur.app.domain.player.PlayerEvent;
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway;
import io.github.malczuuu.lemur.app.domain.player.PlayerNotFoundException;
import io.github.malczuuu.lemur.app.domain.player.PlayerRepository;
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
  public Content<PlayerItem> getPlayers() {
    List<Player> players = playerRepository.findAll();
    List<PlayerItem> content = players.stream().map(playerMapper::toPlayerItem).toList();
    return new Content<>(content);
  }

  @Transactional(readOnly = true)
  public PlayerDetails getPlayer(String id) {
    Player player = fetchPlayer(id);
    return playerMapper.toPlayerDetails(player);
  }

  @Transactional
  public Identity createPlayer(PlayerCommand.CreatePlayer command) {
    Player player = new Player(command.name());
    player = playerRepository.save(player);
    playerEventGateway.publish(new PlayerEvent.PlayerCreated(player.getId().getValue()));
    return new Identity(player.getId().getValue());
  }

  @Transactional
  public void updatePlayer(PlayerCommand.UpdatePlayer command) {
    Player player = lockPlayer(command.id());
    player.setName(command.name());
    player.setVersion(command.version());
    playerEventGateway.publish(new PlayerEvent.PlayerUpdated(player.getId().getValue()));
    playerRepository.save(player);
  }

  @Transactional
  public void banPlayer(PlayerCommand.BanPlayer command) {
    Player player = lockPlayer(command.id());
    player.ban();
    playerRepository.save(player);
    playerEventGateway.publish(new PlayerEvent.PlayerBanned(player.getId().getValue()));
  }

  @Transactional
  public void unbanPlayer(PlayerCommand.UnbanPlayer command) {
    Player player = lockPlayer(command.id());
    player.unban();
    playerRepository.save(player);
    playerEventGateway.publish(new PlayerEvent.PlayerUnbanned(player.getId().getValue()));
  }

  private Player fetchPlayer(String id) {
    return playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
  }

  private Player lockPlayer(String id) {
    return playerRepository.lockById(id).orElseThrow(PlayerNotFoundException::new);
  }
}
