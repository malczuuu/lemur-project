package io.github.malczuuu.lemur.app.infra.data.jpa;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ThingJpaRepository extends JpaRepository<ThingEntity, Long> {

  @Query(
      """
      select t
      from
          ThingEntity t
      order by
          t.createdDate ASC,
          t.id ASC
      """)
  @Override
  List<ThingEntity> findAll();

  @Query(
      """
      select t
      from
          ThingEntity t
      where
          t.id = :id
      """)
  @Override
  Optional<ThingEntity> findById(Long id);

  @Query(
      """
      select t
      from
          ThingEntity t
      where
          t.id = :id
      """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ThingEntity> lockById(Long id);

  @Override
  <T extends ThingEntity> T save(T item);
}
