package io.github.malczuuu.lemur.app.adapter.rest;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.common.model.Content;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerJpaRepository;
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import io.github.problem4j.core.Problem;
import java.net.URI;
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
  void getPlayers_whenEmpty_returnsEmptyContent() {
    playerRepository.deleteAll();
    ExchangeResult response =
        restClient
            .get()
            .uri("/api/v1/players")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    Content<Object> body =
        jsonMapper.readValue(response.getResponseBodyContent(), new TypeReference<>() {});
    assertThat(body.content()).isEmpty();
  }

  @Test
  void registerPlayer_withValidBody_returns201AndLocation() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"name\":\"alice\",\"displayName\":\"Alice\"}")
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getResponseHeaders().getLocation()).isNotNull();
    assertThat(response.getResponseHeaders().getLocation().getPath())
        .startsWith("/api/v1/players/");

    Identity body = jsonMapper.readValue(response.getResponseBodyContent(), Identity.class);
    assertThat(body.id()).isNotBlank();
  }

  @Test
  void registerPlayer_withMissingName_returns400() {
    ExchangeResult response =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{}")
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(problem.getExtensionValue("errors"))
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .contains(Map.of("field", "name", "error", "must not be blank"));
  }

  @Test
  void banPlayer_existingPlayer_returns204() {
    ExchangeResult response =
        restClient.post().uri("/api/v1/players/{id}/ban", player.getId()).exchange().returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void getPlayer_unknownId_returns404() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/api/v1/players/{id}", "317204561")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_NOT_FOUND"));
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void banPlayer_alreadyBanned_returns409() {
    player.setStatus(PlayerStatus.BANNED.getLabel());
    player = playerRepository.save(player);

    ExchangeResult response =
        restClient.post().uri("/api/v1/players/{id}/ban", player.getId()).exchange().returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_ALREADY_BANNED"));
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  @Test
  void unbanPlayer_bannedPlayer_returns204() {
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
  void unbanPlayer_notBannedPlayer_returns409() {

    ExchangeResult response =
        restClient
            .delete()
            .uri("/api/v1/players/{id}/ban", player.getId())
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    Problem problem = jsonMapper.readValue(response.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_NOT_BANNED"));
    assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  private String registerPlayer(String username, String displayName) {
    ExchangeResult result =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"name\":\"" + username + "\",\"displayName\":\"" + displayName + "\"}")
            .exchange()
            .returnResult();
    assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED);
    return jsonMapper.readValue(result.getResponseBodyContent(), Identity.class).id();
  }
}
