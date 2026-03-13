package io.github.malczuuu.lemur.app.adapter.kafka

import io.github.malczuuu.lemur.app.LemurApplication
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEntity
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEventLogJpaRepository
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerJpaRepository
import io.github.malczuuu.lemur.contract.rest.IdentityResult
import io.github.malczuuu.lemur.contract.rest.player.CreatePlayerDto
import io.github.malczuuu.lemur.contract.rest.player.UpdatePlayerDto
import io.github.malczuuu.lemur.testkit.annotation.ContainerTest
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import java.time.Duration

@ActiveProfiles(profiles = ["test"])
@AutoConfigureRestTestClient
@ContainerTest
@SpringBootTest(classes = [LemurApplication::class])
class PlayerListenerTests :
    KafkaAwareTest,
    PostgresAwareTest {

    @Autowired
    private lateinit var restClient: RestTestClient

    @Autowired
    private lateinit var logRepository: PlayerEventLogJpaRepository

    @Autowired
    private lateinit var playerRepository: PlayerJpaRepository

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    private lateinit var player: PlayerEntity

    @BeforeEach
    fun beforeEach() {
        logRepository.deleteAll()
        playerRepository.deleteAll()

        player = PlayerEntity(name = "john.doe", status = PlayerStatus.ACTIVE.label)
        player = playerRepository.save(player)
    }

    @Test
    fun givenValidBody_whenRegisterPlayer_thenPublishesEventAndLogsIt() {
        val result = restClient.post().uri("/api/v1/players")
            .contentType(APPLICATION_JSON)
            .body(CreatePlayerDto("alice"))
            .exchange()
            .returnResult()
        assertThat(result.status).isEqualTo(HttpStatus.CREATED)

        val playerId = jsonMapper.readValue<IdentityResult>(result.responseBodyContent).id!!

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val playerIdAsLong = playerId.toLong()
                val logs = logRepository.findAllByPlayerId(playerIdAsLong)

                assertThat(logs).hasSize(1)
                assertThat(logs.first().eventType).isEqualTo("PlayerCreated")
            }
    }

    @Test
    fun givenExistingPlayer_whenUpdatingPlayer_thenPublishesEventAndLogsIt() {
        val requestBody = UpdatePlayerDto("Mark", player.version!!)
        val banResult = restClient.put().uri("/api/v1/players/{id}", player.id)
            .body(requestBody)
            .exchange()
            .returnResult()
        assertThat(banResult.status).isEqualTo(HttpStatus.NO_CONTENT)

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val logs = logRepository.findAllByPlayerId(player.id!!)

                assertThat(logs).extracting("eventType").hasSize(1)
                assertThat(logs.first().eventType).isEqualTo("PlayerUpdated")
            }
    }

    @Test
    fun givenExistingPlayer_whenBanPlayer_thenPublishesEventAndLogsIt() {
        val banResult = restClient.post().uri("/api/v1/players/{id}/ban", player.id)
            .exchange()
            .returnResult()
        assertThat(banResult.status).isEqualTo(HttpStatus.NO_CONTENT)

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val logs = logRepository.findAllByPlayerId(player.id!!)

                assertThat(logs).extracting("eventType").hasSize(1)
                assertThat(logs.first().eventType).isEqualTo("PlayerBanned")
            }
    }

    @Test
    fun givenBannedPlayer_whenUnbanPlayer_thenPublishesEventAndLogsIt() {
        player = playerRepository.save(player.copy(status = PlayerStatus.BANNED.label))

        val unbanResult = restClient.delete().uri("/api/v1/players/{id}/ban", player.id)
            .exchange()
            .returnResult()
        assertThat(unbanResult.status).isEqualTo(HttpStatus.NO_CONTENT)

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val logs = logRepository.findAllByPlayerId(player.id!!)

                assertThat(logs).extracting("eventType").hasSize(1)
                assertThat(logs.first().eventType).isEqualTo("PlayerUnbanned")
            }
    }
}
