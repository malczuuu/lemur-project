package io.github.malczuuu.lemur.app.adapter.rest;

import io.github.malczuuu.lemur.app.adapter.rest.support.ThingMapper;
import io.github.malczuuu.lemur.app.domain.thing.ThingService;
import io.github.malczuuu.lemur.app.domain.thing.model.ThingModel;
import io.github.malczuuu.lemur.model.Content;
import io.github.malczuuu.lemur.model.Identity;
import io.github.malczuuu.lemur.model.rest.ThingCreateDto;
import io.github.malczuuu.lemur.model.rest.ThingDto;
import io.github.malczuuu.lemur.model.rest.ThingUpdateDto;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/things")
public class ThingController {

  private final ThingService thingService;

  private final ThingMapper mapper = new ThingMapper();

  public ThingController(ThingService thingService) {
    this.thingService = thingService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Content<ThingModel> getItems() {
    return thingService.getItems();
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ThingDto> getItemById(@PathVariable("id") String id) {
    return ResponseEntity.ok(mapper.toThingDto(thingService.getItemById(id)));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Identity> createItem(@RequestBody @Valid ThingCreateDto requestBody) {
    Identity responseBody = thingService.createItem(mapper.toThingCreateModel(requestBody));
    return ResponseEntity.created(URI.create("/api/things/" + responseBody.id()))
        .body(responseBody);
  }

  @PutMapping(
      path = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateItem(
      @PathVariable("id") String id, @RequestBody @Valid ThingUpdateDto requestBody) {
    thingService.updateItem(id, mapper.toThingUpdateModel(requestBody));
    return ResponseEntity.noContent().build();
  }
}
