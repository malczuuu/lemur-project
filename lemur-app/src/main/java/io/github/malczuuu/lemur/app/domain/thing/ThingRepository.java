package io.github.malczuuu.lemur.app.domain.thing;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
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
  List<ThingEntity> findAllById(Long id, Limit limit);

  default Optional<ThingEntity> findById(Long id) {
    return findAllById(id, Limit.of(1)).stream().findFirst();
  }

  <T extends ThingEntity> T save(T item);
}
