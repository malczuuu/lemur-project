package e2e

import io.github.malczuuu.lemur.app.LemurApplication
import io.github.malczuuu.lemur.app.core.player.PlayerDetails
import io.github.malczuuu.lemur.app.domain.player.PlayerStatus
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerJpaRepository
import io.github.malczuuu.lemur.contract.rest.IdentityResult
import io.github.malczuuu.lemur.contract.rest.player.CreatePlayerDto
import io.github.malczuuu.lemur.testkit.annotation.ContainerTest
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest
import io.github.problem4j.core.Problem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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

@ActiveProfiles(profiles = ["test"])
@AutoConfigureRestTestClient
@ContainerTest
@SpringBootTest(classes = [LemurApplication::class])
class PlayerBanE2eTests :
    KafkaAwareTest,
    PostgresAwareTest {

    @Autowired
    private lateinit var playerRepository: PlayerJpaRepository

    @Autowired
    private lateinit var restClient: RestTestClient

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @Test
    fun registerPlayer_thenGetAndBan() {
        val playerId = registerPlayerStep()
        retrieveRegisteredPlayerStep(playerId)
        banPlayerStep(playerId)
        retrieveBannedPlayerStep(playerId)
        tryBanningAgainStep(playerId)
    }

    private fun registerPlayerStep(): String {
        val result = restClient.post().uri("/api/v1/players")
            .contentType(MediaType.APPLICATION_JSON)
            .body(CreatePlayerDto("charlie"))
            .exchange()
            .returnResult()

        assertThat(result.status).isEqualTo(HttpStatus.CREATED)
        assertThat(result.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_JSON)
        assertThat(result.responseHeaders.location).isNotNull()
        assertThat(result.responseHeaders.location!!.path).startsWith("/api/v1/players/")

        val identity = jsonMapper.readValue<IdentityResult>(result.responseBodyContent)
        assertThat(identity.id).isNotBlank()

        return identity.id!!
    }

    private fun retrieveRegisteredPlayerStep(playerId: String) {
        val result = restClient.get().uri("/api/v1/players/{id}", playerId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(result.status).isEqualTo(HttpStatus.OK)
        assertThat(result.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val player = jsonMapper.readValue<PlayerDetails>(result.responseBodyContent)
        assertThat(player.name).isEqualTo("charlie")
        assertThat(player.status).isEqualTo(PlayerStatus.ACTIVE)
    }

    private fun banPlayerStep(playerId: String) {
        val result = restClient.post().uri("/api/v1/players/{id}/ban", playerId)
            .exchange()
            .returnResult()

        assertThat(result.status).isEqualTo(HttpStatus.NO_CONTENT)

        val player = playerRepository.findById(playerId.toLong()).orElseThrow()
        assertThat(player.status).isEqualTo(PlayerStatus.BANNED.label)
    }

    private fun retrieveBannedPlayerStep(playerId: String) {
        val result = restClient.get().uri("/api/v1/players/{id}", playerId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(result.status).isEqualTo(HttpStatus.OK)
        assertThat(result.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val banned = jsonMapper.readValue<PlayerDetails>(result.responseBodyContent)
        assertThat(banned.status).isEqualTo(PlayerStatus.BANNED)
    }

    private fun tryBanningAgainStep(playerId: String) {
        val result = restClient.post().uri("/api/v1/players/{id}/ban", playerId)
            .exchange()
            .returnResult()

        assertThat(result.status).isEqualTo(HttpStatus.CONFLICT)
        assertThat(result.responseHeaders.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        val problem = jsonMapper.readValue<Problem>(result.responseBodyContent)
        assertThat(problem.type).isEqualTo(URI.create("PLAYER_ALREADY_BANNED"))
    }
}
