package io.github.malczuuu.lemur.app.adapter.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.common.model.Identity;
import io.github.malczuuu.lemur.app.contract.rest.player.UpdatePlayerDto;
import io.github.malczuuu.lemur.app.core.CreatePlayerModel;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEventLogEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEventLogJpaRepository;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerJpaRepository;
import io.github.malczuuu.lemur.testkit.annotation.ContainerTest;
import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest;
import io.github.malczuuu.lemur.testkit.kafka.TestKafkaConsumer;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles(profiles = {"test"})
@AutoConfigureRestTestClient
@ContainerTest
@SpringBootTest(classes = {LemurApplication.class})
class PlayerListenerTests implements KafkaAwareTest, PostgresAwareTest {

  @Autowired private RestTestClient restClient;
  @Autowired private PlayerEventLogJpaRepository logRepository;
  @Autowired private PlayerJpaRepository playerRepository;
  @Autowired private JsonMapper jsonMapper;

  @TestListener("${lemur-app.kafka.topic.player-events}")
  private TestKafkaConsumer kafkaConsumer;

  private PlayerEntity player;

  @BeforeEach
  void beforeEach() {
    logRepository.deleteAll();
    playerRepository.deleteAll();

    player = new PlayerEntity();
    player.setName("john.doe");
    player.setStatus(PlayerStatus.ACTIVE.getLabel());
    player = playerRepository.save(player);
  }

  @Test
  void givenValidBody_whenRegisterPlayer_thenPublishesEventAndLogsIt() {
    ExchangeResult result =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body(new CreatePlayerModel("alice"))
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
              assertThat(logs.getFirst().getEventType()).isEqualTo("PlayerCreated");
            });
  }

  @Test
  void givenExistingPlayer_whenUpdatingPlayer_thenPublishesEventAndLogsIt() {
    UpdatePlayerDto requestBody = new UpdatePlayerDto("Mark", player.getVersion());
    ExchangeResult banResult =
        restClient
            .put()
            .uri("/api/v1/players/{id}", player.getId())
            .body(requestBody)
            .exchange()
            .returnResult();
    assertThat(banResult.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<PlayerEventLogEntity> logs = logRepository.findAllByPlayerId(player.getId());

              assertThat(logs).extracting(PlayerEventLogEntity::getEventType).hasSize(1);
              assertThat(logs.getFirst().getEventType()).isEqualTo("PlayerUpdated");
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
