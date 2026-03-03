package io.github.malczuuu.lemur.app.adapter.rest.support;

import io.github.malczuuu.lemur.app.domain.thing.model.ThingCreateModel;
import io.github.malczuuu.lemur.app.domain.thing.model.ThingModel;
import io.github.malczuuu.lemur.app.domain.thing.model.ThingUpdateModel;
import io.github.malczuuu.lemur.model.rest.ThingCreateDto;
import io.github.malczuuu.lemur.model.rest.ThingDto;
import io.github.malczuuu.lemur.model.rest.ThingUpdateDto;

public class ThingMapper {

  public ThingDto toThingDto(ThingModel thing) {
    return new ThingDto(thing.id(), thing.name(), thing.description());
  }

  public ThingCreateModel toThingCreateModel(ThingCreateDto thing) {
    return new ThingCreateModel(thing.name(), thing.description());
  }

  public ThingUpdateModel toThingUpdateModel(ThingUpdateDto thing) {
    return new ThingUpdateModel(thing.name(), thing.description());
  }
}
