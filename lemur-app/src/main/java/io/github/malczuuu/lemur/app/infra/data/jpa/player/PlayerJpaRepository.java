package io.github.malczuuu.lemur.app.infra.data.jpa.player;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, Long> {

  @Query(
      """
      select p
      from
          PlayerEntity p
      order by
          p.createdDate ASC,
          p.id ASC
      """)
  @Override
  List<PlayerEntity> findAll();

  @Query(
      """
      select p
      from
          PlayerEntity p
      where
          p.id = :id
      """)
  @Override
  Optional<PlayerEntity> findById(Long id);

  @Query(
      """
      select p
       from
          PlayerEntity p
      where
          p.id = :id
      """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<PlayerEntity> lockById(Long id);

  @Override
  <T extends PlayerEntity> T save(T entity);
}
