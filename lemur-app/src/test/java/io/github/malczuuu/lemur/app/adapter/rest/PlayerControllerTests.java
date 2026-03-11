package io.github.malczuuu.lemur.app.adapter.rest;

import static io.github.malczuuu.lemur.app.common.message.MessageHeader.EVENT_TYPE_HEADER;
import static io.github.malczuuu.lemur.app.common.message.MessageHeader.findHeader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.common.model.Content;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.contract.rest.player.CreatePlayerDto;
import io.github.malczuuu.lemur.app.contract.rest.player.PlayerDto;
import io.github.malczuuu.lemur.app.contract.rest.player.UpdatePlayerDto;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerJpaRepository;
import io.github.malczuuu.lemur.testkit.annotation.ContainerTest;
import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest;
import io.github.malczuuu.lemur.testkit.kafka.TestKafkaConsumer;
import io.github.problem4j.core.Problem;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles(profiles = {"test"})
@AutoConfigureRestTestClient
@ContainerTest
@SpringBootTest(classes = {LemurApplication.class})
class PlayerControllerTests implements KafkaAwareTest, PostgresAwareTest {

  @Autowired private RestTestClient restClient;
  @Autowired private PlayerJpaRepository playerRepository;
  @Autowired private JsonMapper jsonMapper;

  @TestListener("${lemur-app.kafka.topic.player-events}")
  private TestKafkaConsumer kafkaConsumer;

  private PlayerEntity player;

  @BeforeEach
  void beforeEach() {
    playerRepository.deleteAll();
    kafkaConsumer.clear();

    player = new PlayerEntity();
    player.setName("john.doe");
    player.setStatus(PlayerStatus.ACTIVE.getLabel());
    player = playerRepository.save(player);
  }

  @Test
  void givenMultiplePlayers_whenGetPlayers_thenReturnsSortedByCreatedDateAscThenIdAsc() {
    playerRepository.deleteAll();

    String[] names = {"alpha", "beta", "gamma", "delta"};
    List<PlayerEntity> saved = new ArrayList<>();
    for (String name : names) {
      PlayerEntity entity = new PlayerEntity();
      entity.setName(name);
      entity.setStatus(PlayerStatus.ACTIVE.getLabel());
      saved.add(playerRepository.save(entity));
      await().pollDelay(Duration.ofMillis(25)).until(() -> true);
    }

    ExchangeResult response =
        restClient.get().uri("/api/v1/players").accept(APPLICATION_JSON).exchange().returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_JSON);

    Content<PlayerDto> body =
        jsonMapper.readValue(response.getResponseBodyContent(), new TypeReference<>() {});
    assertThat(body.content()).hasSize(saved.size());
    for (int i = 0; i < saved.size(); i++) {
      assertThat(body.content().get(i).id()).isEqualTo(String.valueOf(saved.get(i).getId()));
    }
  }

  @Test
  void givenNoPlayers_whenGetPlayers_thenReturnsEmptyContent() {
    playerRepository.deleteAll();

    ExchangeResult response =
        restClient.get().uri("/api/v1/players").accept(APPLICATION_JSON).exchange().returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_JSON);

    Content<Object> body =
        jsonMapper.readValue(response.getResponseBodyContent(), new TypeReference<>() {});
    assertThat(body.content()).isEmpty();
  }

  @Test
  void givenExistingId_whenGetPlayer_thenReturns200AndPlayer() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/api/v1/players/{id}", player.getId())
            .accept(APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_JSON);

    PlayerDto body = jsonMapper.readValue(response.getResponseBodyContent(), PlayerDto.class);
    assertThat(body.id()).isEqualTo(String.valueOf(player.getId()));
    assertThat(body.name()).isEqualTo(player.getName());
    assertThat(body.rating()).isEqualTo(player.getRating());
    assertThat(body.status()).isEqualTo(player.getStatus());
  }

  @Test
  void givenUnknownId_whenGetPlayer_thenReturns404() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/api/v1/players/{id}", "317204561")
            .accept(APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_NOT_FOUND"));
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void givenValidBody_whenCreatingPlayer_thenReturns201AndLocation() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body(new CreatePlayerDto("Alice"))
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_JSON);

    assertThat(response.getResponseHeaders().getLocation()).isNotNull();
    assertThat(response.getResponseHeaders().getLocation().getPath())
        .startsWith("/api/v1/players/");

    Identity body = jsonMapper.readValue(response.getResponseBodyContent(), Identity.class);
    assertThat(body.id()).isNotBlank();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " "})
  @NullSource
  void givenInvalidName_whenCreatingPlayer_thenReturns400(String name) {
    Map<String, Object> updateBody = new HashMap<>();
    updateBody.put("name", name);

    ExchangeResult response =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(problem.getExtensionValue("errors"))
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .contains(Map.of("field", "name", "error", "must not be blank"));
  }

  @Test
  void givenUnknownId_whenUpdatePlayer_thenReturns404() {
    var updateBody = new UpdatePlayerDto("newName", 0L);
    ExchangeResult response =
        restClient
            .put()
            .uri("/api/v1/players/{id}", "999999999")
            .contentType(APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_NOT_FOUND"));
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void givenValidBody_whenUpdatePlayer_thenReturns204AndUpdatesPlayer() {
    var updateBody = new UpdatePlayerDto("updatedName", 0L);
    ExchangeResult response =
        restClient
            .put()
            .uri("/api/v1/players/{id}", player.getId())
            .contentType(APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

    PlayerEntity updated = playerRepository.findById(player.getId()).orElseThrow();
    assertThat(updated.getName()).isEqualTo("updatedName");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " "})
  @NullSource
  void givenInvalidName_whenUpdatePlayer_thenReturns400(String name) {
    Map<String, Object> updateBody = new HashMap<>();
    updateBody.put("name", name);
    updateBody.put("version", 0L);

    ExchangeResult response =
        restClient
            .put()
            .uri("/api/v1/players/{id}", player.getId())
            .contentType(APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(problem.getExtensionValue("errors"))
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .anySatisfy(
            e -> assertThat(e).isEqualTo(Map.of("field", "name", "error", "must not be blank")));
  }

  @ParameterizedTest
  @ValueSource(longs = {-1L})
  @NullSource
  void givenInvalidName_whenUpdatePlayer_thenReturns400(Long version) {
    Map<String, Object> updateBody = new HashMap<>();
    updateBody.put("name", "Alice");
    updateBody.put("version", version);

    String error = version == null ? "must not be null" : "must be greater than or equal to 0";

    ExchangeResult response =
        restClient
            .put()
            .uri("/api/v1/players/{id}", player.getId())
            .contentType(APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(problem.getExtensionValue("errors"))
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .anySatisfy(e -> assertThat(e).isEqualTo(Map.of("field", "version", "error", error)));
  }

  @Test
  void givenExistingPlayer_whenBanPlayer_thenReturns204() {
    ExchangeResult response =
        restClient.post().uri("/api/v1/players/{id}/ban", player.getId()).exchange().returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void givenAlreadyBannedPlayer_whenBanPlayer_thenReturns409() {
    player.setStatus(PlayerStatus.BANNED.getLabel());
    player = playerRepository.save(player);

    ExchangeResult response =
        restClient.post().uri("/api/v1/players/{id}/ban", player.getId()).exchange().returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_ALREADY_BANNED"));
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  @Test
  void givenBannedPlayer_whenUnbanPlayer_thenReturns204() {
    player.setStatus(PlayerStatus.BANNED.getLabel());
    player = playerRepository.save(player);

    ExchangeResult response =
        restClient
            .delete()
            .uri("/api/v1/players/{id}/ban", player.getId())
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void givenNotBannedPlayer_whenUnbanPlayer_thenReturns409() {
    ExchangeResult response =
        restClient
            .delete()
            .uri("/api/v1/players/{id}/ban", player.getId())
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_NOT_BANNED"));
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  @Test
  void givenValidBody_whenCreatingPlayer_thenPublishesPlayerCreatedEvent() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body(new CreatePlayerDto("kafka.user"))
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
    Identity body = jsonMapper.readValue(response.getResponseBodyContent(), Identity.class);

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              assertThat(records)
                  .anySatisfy(
                      r -> {
                        assertThat(findHeader(r, EVENT_TYPE_HEADER)).hasValue("PlayerCreated");
                        assertThat(r.key()).isEqualTo(body.id());
                      });
            });
  }

  @Test
  void givenExistingPlayer_whenUpdatePlayer_thenPublishesPlayerUpdatedEvent() {
    var updateBody = new UpdatePlayerDto("updatedName", 0L);
    ExchangeResult response =
        restClient
            .put()
            .uri("/api/v1/players/{id}", player.getId())
            .contentType(APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              assertThat(records)
                  .anySatisfy(
                      r -> {
                        assertThat(findHeader(r, EVENT_TYPE_HEADER)).hasValue("PlayerUpdated");
                        assertThat(r.key()).isEqualTo(String.valueOf(player.getId()));
                      });
            });
  }

  @Test
  void givenExistingPlayer_whenBanPlayer_thenPublishesPlayerBannedEvent() {
    restClient.post().uri("/api/v1/players/{id}/ban", player.getId()).exchange().returnResult();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              assertThat(records)
                  .anySatisfy(
                      r -> {
                        assertThat(findHeader(r, EVENT_TYPE_HEADER)).hasValue("PlayerBanned");
                        assertThat(r.key()).isEqualTo(String.valueOf(player.getId()));
                      });
            });
  }

  @Test
  void givenBannedPlayer_whenUnbanPlayer_thenPublishesPlayerUnbannedEvent() {
    player.setStatus(PlayerStatus.BANNED.getLabel());
    player = playerRepository.save(player);

    restClient.delete().uri("/api/v1/players/{id}/ban", player.getId()).exchange().returnResult();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              assertThat(records)
                  .anySatisfy(
                      r -> {
                        assertThat(findHeader(r, EVENT_TYPE_HEADER)).hasValue("PlayerUnbanned");
                        assertThat(r.key()).isEqualTo(String.valueOf(player.getId()));
                      });
            });
  }
}
