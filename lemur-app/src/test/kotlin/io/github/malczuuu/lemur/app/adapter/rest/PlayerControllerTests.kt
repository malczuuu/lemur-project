package io.github.malczuuu.lemur.app.adapter.rest

import io.github.malczuuu.lemur.app.LemurApplication
import io.github.malczuuu.lemur.app.common.findHeader
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEntity
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerJpaRepository
import io.github.malczuuu.lemur.contract.message.MessageHeaders.EVENT_TYPE_HEADER
import io.github.malczuuu.lemur.contract.rest.ContentResult
import io.github.malczuuu.lemur.contract.rest.IdentityResult
import io.github.malczuuu.lemur.contract.rest.player.CreatePlayerDto
import io.github.malczuuu.lemur.contract.rest.player.PlayerDto
import io.github.malczuuu.lemur.contract.rest.player.PlayerItemDto
import io.github.malczuuu.lemur.contract.rest.player.UpdatePlayerDto
import io.github.malczuuu.lemur.testkit.annotation.ContainerTest
import io.github.malczuuu.lemur.testkit.annotation.TestListener
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest
import io.github.malczuuu.lemur.testkit.kafka.TestKafkaConsumer
import io.github.problem4j.core.Problem
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import java.net.URI
import java.time.Duration

@ActiveProfiles(profiles = ["test"])
@AutoConfigureRestTestClient
@ContainerTest
@SpringBootTest(classes = [LemurApplication::class])
class PlayerControllerTests :
    KafkaAwareTest,
    PostgresAwareTest {

    @Autowired
    private lateinit var restClient: RestTestClient

    @Autowired
    private lateinit var playerRepository: PlayerJpaRepository

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @TestListener($$"${lemur-app.kafka.topic.player-events}")
    private lateinit var kafkaConsumer: TestKafkaConsumer

    private lateinit var player: PlayerEntity

    @BeforeEach
    fun beforeEach() {
        playerRepository.deleteAll()
        kafkaConsumer.clear()

        player = PlayerEntity(name = "john.doe", status = PlayerStatus.ACTIVE.label)
        player = playerRepository.save(player)
    }

    @Test
    fun givenMultiplePlayers_whenGetPlayers_thenReturnsSortedByCreatedDateAscThenIdAsc() {
        playerRepository.deleteAll()

        val names = listOf("alpha", "beta", "gamma", "delta")
        val saved = mutableListOf<PlayerEntity>()
        for (name in names) {
            val entity = PlayerEntity(name = name, status = PlayerStatus.ACTIVE.label)
            saved.add(playerRepository.save(entity))
            await().pollDelay(Duration.ofMillis(25)).until { true }
        }

        val response = restClient.get().uri("/api/v1/players")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body: ContentResult<PlayerItemDto> =
            jsonMapper.readValue<ContentResult<PlayerItemDto>>(response.responseBodyContent)
        assertThat(body.content).hasSize(saved.size)
        for (i in saved.indices) {
            assertThat(body.content[i].id).isEqualTo(saved[i].id.toString())
        }
    }

    @Test
    fun givenNoPlayers_whenGetPlayers_thenReturnsEmptyContent() {
        playerRepository.deleteAll()

        val response = restClient.get().uri("/api/v1/players")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body: ContentResult<Any> =
            jsonMapper.readValue<ContentResult<Any>>(response.responseBodyContent)
        assertThat(body.content).isEmpty()
    }

    @Test
    fun givenExistingId_whenGetPlayer_thenReturns200AndPlayer() {
        val response = restClient.get().uri("/api/v1/players/{id}", player.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = jsonMapper.readValue<PlayerDto>(response.responseBodyContent)
        assertThat(body.id).isEqualTo(player.id.toString())
        assertThat(body.name).isEqualTo(player.name)
        assertThat(body.rating).isEqualTo(player.rating)
        assertThat(body.status).isEqualTo(player.status)
    }

    @Test
    fun givenUnknownId_whenGetPlayer_thenReturns404() {
        val response = restClient.get().uri("/api/v1/players/{id}", "317204561")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.type).isEqualTo(URI.create("PLAYER_NOT_FOUND"))
        assertThat(problem.status).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun givenValidBody_whenCreatingPlayer_thenReturns201AndLocation() {
        val response = restClient.post().uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body(CreatePlayerDto("Alice"))
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.CREATED)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_JSON)
        assertThat(response.responseHeaders.location).isNotNull()
        assertThat(response.responseHeaders.location!!.path).startsWith("/api/v1/players/")

        val body = jsonMapper.readValue<IdentityResult>(response.responseBodyContent)
        assertThat(body.id).isNotBlank()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun givenInvalidName_whenCreatingPlayer_thenReturns400(name: String?) {
        val updateBody = mapOf("name" to name)
        val expectedError = if (name == null) "must not be null" else "must not be blank"

        val response = restClient.post().uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(problem.getExtensionValue("errors"))
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(mapOf("field" to "name", "error" to expectedError))
    }

    @Test
    fun givenNullName_whenCreatingPlayer_thenReturns400() {
        val updateBody = mapOf("name" to null)

        val response = restClient.post().uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(problem.getExtensionValue("property")).isEqualTo("name")
        assertThat(problem.getExtensionValue("kind")).isEqualTo("string")
    }

    @Test
    fun givenUnknownId_whenUpdatePlayer_thenReturns404() {
        val updateBody = UpdatePlayerDto("newName", 0L)
        val response = restClient.put().uri("/api/v1/players/{id}", "999999999")
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.type).isEqualTo(URI.create("PLAYER_NOT_FOUND"))
        assertThat(problem.status).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun givenValidBody_whenUpdatePlayer_thenReturns204AndUpdatesPlayer() {
        val updateBody = UpdatePlayerDto("updatedName", 0L)
        val response = restClient.put().uri("/api/v1/players/{id}", player.id)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.NO_CONTENT)

        val updated = playerRepository.findById(player.id!!).orElseThrow()
        assertThat(updated.name).isEqualTo("updatedName")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun givenInvalidName_whenUpdatePlayer_thenReturns400(name: String) {
        val updateBody = mapOf("name" to name, "version" to 0L)

        val response = restClient.put().uri("/api/v1/players/{id}", player.id)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(problem.getExtensionValue("errors"))
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .anySatisfy { e ->
                assertThat(e).isEqualTo(mapOf("field" to "name", "error" to "must not be blank"))
            }
    }

    @Test
    fun givenNullName_whenUpdatePlayer_thenReturns400() {
        val updateBody = mapOf("name" to null, "version" to 0L)

        val response = restClient.put().uri("/api/v1/players/{id}", player.id)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(problem.getExtensionValue("property")).isEqualTo("name")
        assertThat(problem.getExtensionValue("kind")).isEqualTo("string")
    }

    @ParameterizedTest
    @ValueSource(longs = [-1L, -200L])
    fun givenInvalidVersion_whenUpdatePlayer_thenReturns400(version: Long) {
        val updateBody = mapOf("name" to "Alice", "version" to version)

        val response = restClient.put().uri("/api/v1/players/{id}", player.id)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(problem.getExtensionValue("errors"))
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .anySatisfy { e ->
                assertThat(e).isEqualTo(mapOf("field" to "version", "error" to "must be greater than or equal to 0"))
            }
    }

    @Test
    fun givenNullVersion_whenUpdatePlayer_thenReturns400() {
        val updateBody = mapOf("name" to "Alice", "version" to null)

        val response = restClient.put().uri("/api/v1/players/{id}", player.id)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(problem.getExtensionValue("property")).isEqualTo("version")
        assertThat(problem.getExtensionValue("kind")).isEqualTo("integer")
    }

    @Test
    fun givenExistingPlayer_whenBanPlayer_thenReturns204() {
        val response = restClient.post().uri("/api/v1/players/{id}/ban", player.id)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun givenAlreadyBannedPlayer_whenBanPlayer_thenReturns409() {
        player = playerRepository.save(player.copy(status = PlayerStatus.BANNED.label))

        val response = restClient.post().uri("/api/v1/players/{id}/ban", player.id)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.type).isEqualTo(URI.create("PLAYER_ALREADY_BANNED"))
        assertThat(problem.status).isEqualTo(HttpStatus.CONFLICT.value())
    }

    @Test
    fun givenBannedPlayer_whenUnbanPlayer_thenReturns204() {
        player = playerRepository.save(player.copy(status = PlayerStatus.BANNED.label))

        val response = restClient.delete().uri("/api/v1/players/{id}/ban", player.id)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun givenNotBannedPlayer_whenUnbanPlayer_thenReturns409() {
        val response = restClient.delete().uri("/api/v1/players/{id}/ban", player.id)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(response.responseBodyContent)
        assertThat(problem.type).isEqualTo(URI.create("PLAYER_NOT_BANNED"))
        assertThat(problem.status).isEqualTo(HttpStatus.CONFLICT.value())
    }

    @Test
    fun givenValidBody_whenCreatingPlayer_thenPublishesPlayerCreatedEvent() {
        val response = restClient.post().uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body(CreatePlayerDto("kafka.user"))
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.CREATED)
        val body = jsonMapper.readValue<IdentityResult>(response.responseBodyContent)

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val records = kafkaConsumer.poll(Duration.ofMillis(500))
                assertThat(records).anySatisfy { r ->
                    assertThat(r.findHeader(EVENT_TYPE_HEADER)).isEqualTo("PlayerCreated")
                    assertThat(r.key()).isEqualTo(body.id)
                }
            }
    }

    @Test
    fun givenExistingPlayer_whenUpdatePlayer_thenPublishesPlayerUpdatedEvent() {
        val updateBody = UpdatePlayerDto("updatedName", 0L)
        val response = restClient.put().uri("/api/v1/players/{id}", player.id)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updateBody)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.NO_CONTENT)

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val records = kafkaConsumer.poll(Duration.ofMillis(500))
                assertThat(records).anySatisfy { r ->
                    assertThat(r.findHeader(EVENT_TYPE_HEADER)).isEqualTo("PlayerUpdated")
                    assertThat(r.key()).isEqualTo(player.id.toString())
                }
            }
    }

    @Test
    fun givenExistingPlayer_whenBanPlayer_thenPublishesPlayerBannedEvent() {
        restClient.post().uri("/api/v1/players/{id}/ban", player.id).exchange().returnResult()

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val records = kafkaConsumer.poll(Duration.ofMillis(500))
                assertThat(records).anySatisfy { r ->
                    assertThat(r.findHeader(EVENT_TYPE_HEADER)).isEqualTo("PlayerBanned")
                    assertThat(r.key()).isEqualTo(player.id.toString())
                }
            }
    }

    @Test
    fun givenBannedPlayer_whenUnbanPlayer_thenPublishesPlayerUnbannedEvent() {
        player = playerRepository.save(player.copy(status = PlayerStatus.BANNED.label))

        restClient.delete().uri("/api/v1/players/{id}/ban", player.id).exchange().returnResult()

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(300))
            .untilAsserted {
                val records = kafkaConsumer.poll(Duration.ofMillis(500))
                assertThat(records).anySatisfy { r ->
                    assertThat(r.findHeader(EVENT_TYPE_HEADER)).isEqualTo("PlayerUnbanned")
                    assertThat(r.key()).isEqualTo(player.id.toString())
                }
            }
    }
}
