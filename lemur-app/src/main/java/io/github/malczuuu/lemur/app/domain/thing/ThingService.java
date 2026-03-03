package io.github.malczuuu.lemur.app.domain.thing;

import static java.util.Objects.requireNonNull;

import io.github.malczuuu.lemur.app.domain.thing.model.ThingCreateModel;
import io.github.malczuuu.lemur.app.domain.thing.model.ThingModel;
import io.github.malczuuu.lemur.app.domain.thing.model.ThingUpdateModel;
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

  public Content<ThingModel> getItems() {
    List<ThingEntity> entities = thingRepository.findAll();
    List<ThingModel> content = entities.stream().map(this::toThingModel).toList();
    return new Content<>(content);
  }

  public ThingModel getItemById(String id) {
    ThingEntity entity = fetchItem(id);
    return toThingModel(entity);
  }

  public Identity createItem(ThingCreateModel thing) {
    ThingEntity entity = new ThingEntity(thing.name(), thing.description());
    entity = thingRepository.save(entity);
    return new Identity(requireNonNull(entity.getId()).toString());
  }

  @Transactional
  public void updateItem(String id, ThingUpdateModel update) {
    ThingEntity entity = lockItem(id);
    entity.setName(update.name());
    entity.setDescription(update.description());
    thingRepository.save(entity);
  }

  private ThingEntity fetchItem(String id) {
    Long idAsLong = Identity.parseLong(id, ThingNotFoundException::new);
    return thingRepository.findById(idAsLong).orElseThrow(ThingNotFoundException::new);
  }

  private ThingEntity lockItem(String id) {
    Long idAsLong = Identity.parseLong(id, ThingNotFoundException::new);
    return thingRepository.lockById(idAsLong).orElseThrow(ThingNotFoundException::new);
  }

  private ThingModel toThingModel(ThingEntity thing) {
    return new ThingModel(
        requireNonNull(thing.getId()).toString(), thing.getName(), thing.getDescription());
  }
}
