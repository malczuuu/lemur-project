package io.github.malczuuu.lemur.app.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerJpaRepository;
import io.github.malczuuu.lemur.model.Identity;
import io.github.malczuuu.lemur.model.rest.PlayerDto;
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import io.github.problem4j.core.Problem;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles(profiles = {"test"})
@PostgresAwareTest
@KafkaAwareTest
@SpringBootTest(classes = {LemurApplication.class})
@AutoConfigureRestTestClient
class PlayerBanE2eTests {

  @Autowired private PlayerJpaRepository playerRepository;
  @Autowired private RestTestClient restClient;
  @Autowired private JsonMapper jsonMapper;

  @Test
  void registerPlayer_thenGetAndBan() {
    String playerId = registerPlayerStep();
    retrieveRegisteredPlayerStep(playerId);
    banPlayerStep(playerId);
    retrieveBannedPlayerStep(playerId);
    tryBanningAgainStep(playerId);
  }

  private String registerPlayerStep() {
    ExchangeResult result =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"name\":\"charlie\"}")
            .exchange()
            .returnResult();

    assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED);
    assertThat(result.getResponseHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(result.getResponseHeaders().getLocation()).isNotNull();
    assertThat(result.getResponseHeaders().getLocation().getPath()).startsWith("/api/v1/players/");

    Identity identity = jsonMapper.readValue(result.getResponseBodyContent(), Identity.class);
    assertThat(identity.id()).isNotBlank();

    return identity.id();
  }

  private void retrieveRegisteredPlayerStep(String playerId) {
    ExchangeResult result =
        restClient
            .get()
            .uri("/api/v1/players/{id}", playerId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(result.getResponseHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    PlayerDto player = jsonMapper.readValue(result.getResponseBodyContent(), PlayerDto.class);
    assertThat(player.name()).isEqualTo("charlie");
    assertThat(player.status()).isEqualTo(PlayerStatus.ACTIVE.getLabel());
  }

  private void banPlayerStep(String playerId) {
    ExchangeResult result =
        restClient.post().uri("/api/v1/players/{id}/ban", playerId).exchange().returnResult();

    assertThat(result.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

    PlayerEntity player = playerRepository.findById(Long.parseLong(playerId)).orElseThrow();
    assertThat(player.getStatus()).isEqualTo(PlayerStatus.BANNED.getLabel());
  }

  private void retrieveBannedPlayerStep(String playerId) {
    ExchangeResult result =
        restClient
            .get()
            .uri("/api/v1/players/{id}", playerId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(result.getResponseHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    PlayerDto banned = jsonMapper.readValue(result.getResponseBodyContent(), PlayerDto.class);
    assertThat(banned.status()).isEqualTo(PlayerStatus.BANNED.getLabel());
  }

  private void tryBanningAgainStep(String playerId) {
    ExchangeResult result =
        restClient.post().uri("/api/v1/players/{id}/ban", playerId).exchange().returnResult();

    assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(result.getResponseHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem = jsonMapper.readValue(result.getResponseBodyContent(), Problem.class);
    assertThat(problem.getType()).isEqualTo(URI.create("PLAYER_ALREADY_BANNED"));
  }
}
