package io.github.malczuuu.lemur.app.adapter.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.common.model.Content;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.core.PlayerModel;
import io.github.malczuuu.lemur.app.core.RegisterPlayerModel;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerJpaRepository;
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import io.github.problem4j.core.Problem;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@PostgresAwareTest
@KafkaAwareTest
@SpringBootTest(classes = {LemurApplication.class})
@AutoConfigureRestTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerControllerTests {

  @Autowired private RestTestClient restClient;
  @Autowired private PlayerJpaRepository playerRepository;
  @Autowired private JsonMapper jsonMapper;

  private PlayerEntity player;

  @BeforeAll
  void beforeAll() {
    playerRepository.deleteAll();
  }

  @BeforeEach
  void beforeEach() {
    player = new PlayerEntity();
    player.setName("john.doe");
    player.setStatus(PlayerStatus.ACTIVE.getLabel());
    player = playerRepository.save(player);
  }

  @AfterEach
  void afterEach() {
    playerRepository.deleteAll();
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

    Content<PlayerModel> body =
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

    PlayerModel body = jsonMapper.readValue(response.getResponseBodyContent(), PlayerModel.class);
    assertThat(body.id()).isEqualTo(String.valueOf(player.getId()));
    assertThat(body.name()).isEqualTo(player.getName());
    assertThat(body.rating()).isEqualTo(player.getRating());
    assertThat(body.status().getLabel()).isEqualTo(player.getStatus());
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
  void givenValidBody_whenRegisterPlayer_thenReturns201AndLocation() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body(new RegisterPlayerModel("Alice"))
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

  @Test
  void givenMissingName_whenRegisterPlayer_thenReturns400() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body("{}")
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
}
