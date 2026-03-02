package io.github.malczuuu.lemur.app.domain.thing;

import static java.util.Objects.requireNonNull;

import io.github.malczuuu.lemur.app.common.Content;
import io.github.malczuuu.lemur.app.common.Identity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ThingService {

  private final ThingRepository thingRepository;

  public ThingService(ThingRepository thingRepository) {
    this.thingRepository = thingRepository;
  }

  public Content<ThingDto> getItems() {
    List<ThingEntity> entities = thingRepository.findAll();
    List<ThingDto> content = entities.stream().map(this::toItemDto).toList();
    return new Content<>(content);
  }

  public ThingDto getItemById(String id) {
    ThingEntity entity = fetchItem(id);
    return toItemDto(entity);
  }

  public Identity createItem(ThingCreateDto item) {
    ThingEntity entity = new ThingEntity(item.name(), item.description());
    entity = thingRepository.save(entity);
    return new Identity(requireNonNull(entity.getId()).toString());
  }

  private ThingEntity fetchItem(String id) {
    Long idAsLong = Identity.safeParseLong(id).orElseThrow(ThingNotFoundException::new);
    return thingRepository.findById(idAsLong).orElseThrow(ThingNotFoundException::new);
  }

  private ThingDto toItemDto(ThingEntity item) {
    return new ThingDto(
        requireNonNull(item.getId()).toString(), item.getName(), item.getDescription());
  }
}
