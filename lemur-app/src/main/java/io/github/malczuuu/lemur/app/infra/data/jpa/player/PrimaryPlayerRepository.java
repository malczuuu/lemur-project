package io.github.malczuuu.lemur.app.infra.data.jpa.player;

import static io.github.malczuuu.lemur.app.common.NumberUtils.safeParseLong;

import io.github.malczuuu.lemur.app.domain.player.Player;
import io.github.malczuuu.lemur.app.domain.player.PlayerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class PrimaryPlayerRepository implements PlayerRepository {

  private final PlayerJpaRepository playerRepository;

  private final PlayerEntityMapper playerMapper = new PlayerEntityMapper();

  PrimaryPlayerRepository(PlayerJpaRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  @Override
  public List<Player> findAll() {
    return playerRepository.findAll().stream().map(playerMapper::toPlayer).toList();
  }

  @Override
  public Optional<Player> findById(String id) {
    return safeParseLong(id).flatMap(playerRepository::findById).map(playerMapper::toPlayer);
  }

  @Override
  public Optional<Player> lockById(String id) {
    return safeParseLong(id).flatMap(playerRepository::lockById).map(playerMapper::toPlayer);
  }

  @Override
  public Player save(Player player) {
    PlayerEntity entity = playerMapper.toEntity(player);
    entity = playerRepository.save(entity);
    return playerMapper.toPlayer(entity);
  }
}
