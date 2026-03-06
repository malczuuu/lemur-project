package io.github.malczuuu.lemur.app.core;

import io.github.malczuuu.lemur.app.core.model.ThingCreateModel;
import io.github.malczuuu.lemur.app.core.model.ThingModel;
import io.github.malczuuu.lemur.app.core.model.ThingUpdateModel;
import io.github.malczuuu.lemur.app.domain.thing.Thing;
import io.github.malczuuu.lemur.app.domain.thing.ThingEventGateway;
import io.github.malczuuu.lemur.app.domain.thing.ThingNotFoundException;
import io.github.malczuuu.lemur.app.domain.thing.ThingRepository;
import io.github.malczuuu.lemur.model.Content;
import io.github.malczuuu.lemur.model.Identity;
import io.github.malczuuu.lemur.model.message.ThingCreatedEvent;
import io.github.malczuuu.lemur.model.message.ThingUpdatedEvent;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThingService {

  private final ThingRepository thingRepository;
  private final ThingEventGateway thingEventGateway;

  public ThingService(ThingRepository thingRepository, ThingEventGateway thingEventGateway) {
    this.thingRepository = thingRepository;
    this.thingEventGateway = thingEventGateway;
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

  private final AtomicInteger counter = new AtomicInteger(0);

  @Transactional
  public Identity createThing(ThingCreateModel thing) {
    Thing entity = new Thing(thing.name(), thing.description());
    entity = thingRepository.save(entity);
    thingEventGateway.publish(toThingCreatedEvent(entity));
    try {
      Thread.sleep(ThreadLocalRandom.current().nextInt(800, 1200));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    if (counter.incrementAndGet() % 2 == 0) {
      throw new RuntimeException("simulated failure");
    }
    return new Identity(entity.getId().getValue());
  }

  @Transactional
  public void updateThing(String id, ThingUpdateModel update) {
    Thing entity = lockThing(id);
    entity.setName(update.name());
    entity.setDescription(update.description());
    entity = thingRepository.save(entity);
    thingEventGateway.publish(toThingUpdatedEvent(entity));
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

  private ThingCreatedEvent toThingCreatedEvent(Thing entity) {
    return new ThingCreatedEvent(
        entity.getId().getValue(), entity.getName(), entity.getDescription(), entity.getVersion());
  }

  private ThingUpdatedEvent toThingUpdatedEvent(Thing entity) {
    return new ThingUpdatedEvent(
        entity.getId().getValue(), entity.getName(), entity.getDescription(), entity.getVersion());
  }
}
