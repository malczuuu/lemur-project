package io.github.malczuuu.lemur.app.infra.data.jpa.player;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerEventLogJpaRepository extends JpaRepository<PlayerEventLogEntity, Long> {

  List<PlayerEventLogEntity> findAllByPlayerId(Long playerId);
}
