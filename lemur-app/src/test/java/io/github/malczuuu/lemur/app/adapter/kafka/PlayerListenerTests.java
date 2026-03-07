package io.github.malczuuu.lemur.app.adapter.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEventLogEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEventLogJpaRepository;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerJpaRepository;
import io.github.malczuuu.lemur.model.Identity;
import io.github.malczuuu.lemur.model.rest.RegisterPlayerDto;
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
  void registerPlayer_publishesEventAndLogsIt() {
    ExchangeResult result =
        restClient
            .post()
            .uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RegisterPlayerDto("alice"))
            .exchange()
            .returnResult();
    assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED);
    String playerId = jsonMapper.readValue(result.getResponseBodyContent(), Identity.class).id();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<PlayerEventLogEntity> logs =
                  logRepository.findAllByPlayerId(Long.parseLong(playerId));
              assertThat(logs).hasSize(1);
              assertThat(logs.getFirst().getEventType()).isEqualTo("PlayerRegistered");
            });
  }

  @Test
  void banPlayer_publishesBothEventsAndLogsThem() {
    String playerId = player.getId().toString();

    ExchangeResult banResult =
        restClient.post().uri("/api/v1/players/" + playerId + "/ban").exchange().returnResult();
    assertThat(banResult.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(300))
        .untilAsserted(
            () -> {
              List<PlayerEventLogEntity> logs = logRepository.findAllByPlayerId(player.getId());
              assertThat(logs)
                  .extracting(PlayerEventLogEntity::getEventType)
                  .containsExactlyInAnyOrder("PlayerBanned");
            });
  }
}
