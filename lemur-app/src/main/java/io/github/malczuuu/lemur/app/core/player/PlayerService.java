package io.github.malczuuu.lemur.app.core.player;

import io.github.malczuuu.lemur.app.domain.player.Player;
import io.github.malczuuu.lemur.app.domain.player.PlayerBanned;
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway;
import io.github.malczuuu.lemur.app.domain.player.PlayerNotFoundException;
import io.github.malczuuu.lemur.app.domain.player.PlayerRegistered;
import io.github.malczuuu.lemur.app.domain.player.PlayerRepository;
import io.github.malczuuu.lemur.model.Content;
import io.github.malczuuu.lemur.model.Identity;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerEventGateway playerEventGateway;

  public PlayerService(PlayerRepository playerRepository, PlayerEventGateway playerEventGateway) {
    this.playerRepository = playerRepository;
    this.playerEventGateway = playerEventGateway;
  }

  public Content<PlayerModel> getPlayers() {
    List<Player> players = playerRepository.findAll();
    List<PlayerModel> content = players.stream().map(this::toModel).toList();
    return new Content<>(content);
  }

  public PlayerModel getPlayer(String id) {
    return toModel(fetchPlayer(id));
  }

  @Transactional
  public Identity registerPlayer(RegisterPlayerModel model) {
    Player player = new Player(model.name());
    player = playerRepository.save(player);
    playerEventGateway.publish(new PlayerRegistered(player.getId().getValue()));
    return new Identity(player.getId().getValue());
  }

  @Transactional
  public void banPlayer(String id) {
    Player player = lockPlayer(id);
    player.ban();
    playerRepository.save(player);
    playerEventGateway.publish(new PlayerBanned(player.getId().getValue()));
  }

  private Player fetchPlayer(String id) {
    return playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
  }

  private Player lockPlayer(String id) {
    return playerRepository.lockById(id).orElseThrow(PlayerNotFoundException::new);
  }

  private PlayerModel toModel(Player player) {
    return PlayerModel.from(player);
  }
}
