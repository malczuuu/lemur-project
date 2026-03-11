package io.github.malczuuu.lemur.app.domain.player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

  List<Player> findAll();

  Optional<Player> findById(String id);

  Optional<Player> lockById(String id);

  Player save(Player player);
}
