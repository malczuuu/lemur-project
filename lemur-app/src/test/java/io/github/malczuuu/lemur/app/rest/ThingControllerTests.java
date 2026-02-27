package io.github.malczuuu.lemur.app.rest;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.common.Content;
import io.github.malczuuu.lemur.app.common.Identity;
import io.github.malczuuu.lemur.app.domain.ThingDto;
import io.github.malczuuu.lemur.app.domain.ThingEntity;
import io.github.malczuuu.lemur.app.domain.ThingRepository;
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import io.github.problem4j.core.Problem;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles(profiles = {"test"})
@PostgresAwareTest
@KafkaAwareTest
@SpringBootTest(classes = {LemurApplication.class})
@AutoConfigureRestTestClient
class ThingControllerTests {

  @Autowired private RestTestClient restClient;

  @Autowired private ThingRepository thingRepository;

  @Autowired private JsonMapper jsonMapper;

  private Long existingThingId;

  @BeforeEach
  void beforeEach() {
    ThingEntity entity1 = thingRepository.save(new ThingEntity("thing-1", "desc-1"));
    thingRepository.save(new ThingEntity("thing-2", "desc-2"));
    thingRepository.save(new ThingEntity("thing-3", "desc-3"));
    existingThingId = entity1.getId();
  }

  @AfterEach
  void afterEach() {
    thingRepository.deleteAll();
  }

  @Test
  void givenNoThings_whenGetThings_thenReturnEmptyContent() {
    thingRepository.deleteAll();

    ExchangeResult response =
        restClient
            .get()
            .uri("/api/things")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);

    Content<ThingDto> body =
        jsonMapper.readValue(response.getResponseBodyContent(), new TypeReference<>() {});
    assertThat(body.content()).isEmpty();
  }

  @Test
  void givenValidThing_whenPostThing_thenReturnCreatedStatus() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/things")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"name\":\"thing-new\",\"description\":\"desc-new\"}")
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getResponseHeaders().getLocation()).isNotNull();
    assertThat(response.getResponseHeaders().getLocation().getPath()).startsWith("/api/things/");

    Identity body = jsonMapper.readValue(response.getResponseBodyContent(), Identity.class);
    assertThat(body.id()).isNotNull();
  }

  @Test
  void givenExistingThing_whenGetThingById_thenReturnThing() {
    ExchangeResult getResponse =
        restClient
            .get()
            .uri("/api/things/" + existingThingId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK);

    ThingDto body = jsonMapper.readValue(getResponse.getResponseBodyContent(), ThingDto.class);
    assertThat(body.name()).isEqualTo("thing-1");
    assertThat(body.description()).isEqualTo("desc-1");
  }

  @Test
  void givenExistingThing_whenGetThings_thenReturnListContainingThing() {
    ExchangeResult listResponse =
        restClient
            .get()
            .uri("/api/things")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(listResponse.getStatus()).isEqualTo(HttpStatus.OK);

    Content<ThingDto> body =
        jsonMapper.readValue(listResponse.getResponseBodyContent(), new TypeReference<>() {});
    assertThat(body.content()).hasSize(3);
    assertThat(body.content().toString()).contains("thing-1", "thing-2", "thing-3");
  }

  @Test
  void givenNonExistingId_whenGetThingById_thenReturnNotFound() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/api/things/999999")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(problem.getDetail()).isEqualTo("thing not found");
  }

  @Test
  void givenInvalidId_whenGetThingById_thenReturnNotFound() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/api/things/not-a-number")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(problem.getDetail()).isEqualTo("thing not found");
  }

  @Test
  void givenNullName_whenPostThing_thenReturnBadRequest() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/things")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"description\":\"desc-only\"}")
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(problem.getDetail()).contains("validation failed");
    assertThat(problem.getExtensionValue("errors"))
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .contains(Map.of("field", "name", "error", "must not be null"));
  }

  @Test
  void givenNullDescription_whenPostThing_thenReturnBadRequest() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/things")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"name\":\"name-only\"}")
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(problem.getDetail()).contains("validation failed");
    assertThat(problem.getExtensionValue("errors"))
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .contains(Map.of("field", "description", "error", "must not be null"));
  }
}
