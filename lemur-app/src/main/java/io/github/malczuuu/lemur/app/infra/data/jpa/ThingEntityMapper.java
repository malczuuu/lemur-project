package io.github.malczuuu.lemur.app.infra.data.jpa;

import io.github.malczuuu.lemur.app.domain.DomainId;
import io.github.malczuuu.lemur.app.domain.thing.Thing;

class ThingEntityMapper {

  ThingEntity toThingEntity(Thing thing) {
    if (thing.isNew()) {
      return new ThingEntity(thing.getName(), thing.getDescription());
    } else {
      return new ThingEntity(
          Long.parseLong(thing.getId().getValue()),
          thing.getName(),
          thing.getDescription(),
          thing.getCreatedDate(),
          thing.getLastModifiedDate(),
          thing.getVersion());
    }
  }

  Thing toThing(ThingEntity entity) {
    return new Thing(
        DomainId.of(entity.getId()),
        entity.getName(),
        entity.getDescription(),
        entity.getCreatedDate(),
        entity.getLastModifiedDate(),
        entity.getVersion());
  }
}
