package io.github.malczuuu.lemur.app.infra.data.jpa;

import io.github.malczuuu.lemur.app.domain.thing.Thing;
import io.github.malczuuu.lemur.app.domain.thing.ThingRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class ThingRepositoryImpl implements ThingRepository {

  private final ThingJpaRepository thingJpaRepository;

  private final ThingEntityMapper thingEntityMapper = new ThingEntityMapper();
  private final IdentityMapper identityMapper = new IdentityMapper();

  ThingRepositoryImpl(ThingJpaRepository thingJpaRepository) {
    this.thingJpaRepository = thingJpaRepository;
  }

  @Override
  public List<Thing> findAll() {
    return thingJpaRepository.findAll().stream().map(thingEntityMapper::toThing).toList();
  }

  @Override
  public Optional<Thing> findById(String id) {
    return identityMapper
        .safeParseId(id)
        .flatMap(thingJpaRepository::findById)
        .map(thingEntityMapper::toThing);
  }

  @Override
  public Optional<Thing> lockById(String id) {
    return identityMapper
        .safeParseId(id)
        .flatMap(thingJpaRepository::lockById)
        .map(thingEntityMapper::toThing);
  }

  @Override
  public Thing save(Thing thing) {
    ThingEntity entity = thingEntityMapper.toThingEntity(thing);
    entity = thingJpaRepository.save(entity);
    return thingEntityMapper.toThing(entity);
  }

  @Override
  public void deleteById(String id) {
    identityMapper.safeParseId(id).ifPresent(thingJpaRepository::deleteById);
  }
}
