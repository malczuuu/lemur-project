package io.github.malczuuu.lemur.app.adapter.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.core.RegisterPlayerModel;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEventLogEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEventLogJpaRepository;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerJpaRepository;
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.json.JsonMapper;

@PostgresAwareTest
@KafkaAwareTest
@SpringBootTest(classes = {LemurApplication.class})
@AutoConfigureRestTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerListenerTests {

  @Autowired private RestTestClient restClient;
  @Autowired private PlayerEventLogJpaRepository logRepository;
  @Autowired private PlayerJpaRepository playerRepository;
  @Autowired private JsonMapper jsonMapper;

  private PlayerEntity player;

  @BeforeAll
  void beforeAll() {
    logRepository.deleteAll();
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
    logRepository.deleteAll();
    playerRepository.deleteAll();
  }

  @Test
  void givenValidBody_whenRegisterPlayer_thenPublishesEventAndLogsIt() {
    ExchangeResult result =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body(new RegisterPlayerModel("alice"))
            .exchange()
            .returnResult();
    assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED);

    String playerId = jsonMapper.readValue(result.getResponseBodyContent(), Identity.class).id();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              long playerIdAsLong = Long.parseLong(playerId);
              List<PlayerEventLogEntity> logs = logRepository.findAllByPlayerId(playerIdAsLong);

              assertThat(logs).hasSize(1);
              assertThat(logs.getFirst().getEventType()).isEqualTo("PlayerRegistered");
            });
  }

  @Test
  void givenExistingPlayer_whenBanPlayer_thenPublishesEventAndLogsIt() {
    ExchangeResult banResult =
        restClient.post().uri("/api/v1/players/{id}/ban", player.getId()).exchange().returnResult();
    assertThat(banResult.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<PlayerEventLogEntity> logs = logRepository.findAllByPlayerId(player.getId());

              assertThat(logs).extracting(PlayerEventLogEntity::getEventType).hasSize(1);
              assertThat(logs.getFirst().getEventType()).isEqualTo("PlayerBanned");
            });
  }

  @Test
  void givenBannedPlayer_whenUnbanPlayer_thenPublishesEventAndLogsIt() {
    player.setStatus(PlayerStatus.BANNED.getLabel());
    player = playerRepository.save(player);

    ExchangeResult unbanResult =
        restClient
            .delete()
            .uri("/api/v1/players/{id}/ban", player.getId())
            .exchange()
            .returnResult();
    assertThat(unbanResult.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<PlayerEventLogEntity> logs = logRepository.findAllByPlayerId(player.getId());

              assertThat(logs).extracting(PlayerEventLogEntity::getEventType).hasSize(1);
              assertThat(logs.getFirst().getEventType()).isEqualTo("PlayerUnbanned");
            });
  }
}
