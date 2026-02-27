package io.github.malczuuu.lemur.app.rest;

import io.github.malczuuu.lemur.app.common.Content;
import io.github.malczuuu.lemur.app.common.Identity;
import io.github.malczuuu.lemur.app.domain.ThingCreateDto;
import io.github.malczuuu.lemur.app.domain.ThingDto;
import io.github.malczuuu.lemur.app.domain.ThingService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/things")
public class ThingController {

  private final ThingService thingService;

  public ThingController(ThingService thingService) {
    this.thingService = thingService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Content<ThingDto> getItems() {
    return thingService.getItems();
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ThingDto getItemById(@PathVariable("id") String id) {
    return thingService.getItemById(id);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Identity> createItem(@RequestBody @Valid ThingCreateDto requestBody) {
    Identity responseBody = thingService.createItem(requestBody);
    return ResponseEntity.created(URI.create("/api/things/" + responseBody.id()))
        .body(responseBody);
  }
}
