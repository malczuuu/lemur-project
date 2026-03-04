package io.github.malczuuu.lemur.app.core;

import io.github.malczuuu.lemur.app.core.model.ThingCreateModel;
import io.github.malczuuu.lemur.app.core.model.ThingModel;
import io.github.malczuuu.lemur.app.core.model.ThingUpdateModel;
import io.github.malczuuu.lemur.app.domain.thing.Thing;
import io.github.malczuuu.lemur.app.domain.thing.ThingNotFoundException;
import io.github.malczuuu.lemur.app.domain.thing.ThingRepository;
import io.github.malczuuu.lemur.model.Content;
import io.github.malczuuu.lemur.model.Identity;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThingService {

  private final ThingRepository thingRepository;

  public ThingService(ThingRepository thingRepository) {
    this.thingRepository = thingRepository;
  }

  public Content<ThingModel> getThings() {
    List<Thing> entities = thingRepository.findAll();
    List<ThingModel> content = entities.stream().map(this::toThingModel).toList();
    return new Content<>(content);
  }

  public ThingModel getThingById(String id) {
    Thing entity = fetchThing(id);
    return toThingModel(entity);
  }

  public Identity createThing(ThingCreateModel thing) {
    Thing entity = new Thing(thing.name(), thing.description());
    entity = thingRepository.save(entity);
    return new Identity(entity.getId().getValue());
  }

  @Transactional
  public void updateThing(String id, ThingUpdateModel update) {
    Thing entity = lockThing(id);
    entity.setName(update.name());
    entity.setDescription(update.description());
    thingRepository.save(entity);
  }

  private Thing fetchThing(String id) {
    return thingRepository.findById(id).orElseThrow(ThingNotFoundException::new);
  }

  private Thing lockThing(String id) {
    return thingRepository.lockById(id).orElseThrow(ThingNotFoundException::new);
  }

  private ThingModel toThingModel(Thing thing) {
    return new ThingModel(thing.getId().getValue(), thing.getName(), thing.getDescription());
  }
}
