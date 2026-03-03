package io.github.malczuuu.lemur.app.domain.thing;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ThingRepository extends JpaRepository<ThingEntity, Long> {

  @Query(
      """
      select t
      from
          ThingEntity t
      order by
          t.createdDate ASC,
          t.id ASC
      """)
  List<ThingEntity> findAll();

  @Query(
      """
      select t
      from
          ThingEntity t
      where
          t.id = :id
      """)
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

  <T extends ThingEntity> T save(T item);
}
