package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.core.model.ThingCreateModel;
import io.github.malczuuu.lemur.app.core.model.ThingModel;
import io.github.malczuuu.lemur.app.core.model.ThingUpdateModel;
import io.github.malczuuu.lemur.model.rest.ThingCreateDto;
import io.github.malczuuu.lemur.model.rest.ThingDto;
import io.github.malczuuu.lemur.model.rest.ThingUpdateDto;

class ThingMapper {

  ThingDto toThingDto(ThingModel thing) {
    return new ThingDto(thing.id(), thing.name(), thing.description());
  }

  ThingCreateModel toThingCreateModel(ThingCreateDto thing) {
    return new ThingCreateModel(thing.name(), thing.description());
  }

  ThingUpdateModel toThingUpdateModel(ThingUpdateDto thing) {
    return new ThingUpdateModel(thing.name(), thing.description());
  }
}
