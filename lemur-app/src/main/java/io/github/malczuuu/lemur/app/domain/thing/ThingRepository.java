package io.github.malczuuu.lemur.app.domain.thing;

import java.util.List;
import java.util.Optional;

public interface ThingRepository {

  List<Thing> findAll();

  Optional<Thing> findById(String id);

  Optional<Thing> lockById(String id);

  Thing save(Thing thing);

  void deleteById(String id);
}
