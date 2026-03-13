package io.github.malczuuu.lemur.app.infra

import io.github.malczuuu.lemur.app.LemurApplication
import io.github.malczuuu.lemur.testkit.annotation.ContainerTest
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient

@ActiveProfiles(profiles = ["test"])
@AutoConfigureMetrics
@AutoConfigureRestTestClient
@ContainerTest
@SpringBootTest(classes = [LemurApplication::class])
class ActuatorEndpointsTests :
    KafkaAwareTest,
    PostgresAwareTest {

    @Autowired
    private lateinit var restClient: RestTestClient

    @Test
    fun givenRunningApp_whenGetHealth_thenReturnOk() {
        val response = restClient.get().uri("/actuator/health")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseBodyContent).isNotNull()

        val body = String(response.responseBodyContent)
        assertThat(body).contains("\"status\"")
    }

    @Test
    fun givenRunningApp_whenGetLiveness_thenReturnOk() {
        val response = restClient.get().uri("/actuator/health/liveness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseBodyContent).isNotNull()

        val body = String(response.responseBodyContent)
        assertThat(body).contains("\"status\"")
    }

    @Test
    fun givenRunningApp_whenGetReadiness_thenReturnOk() {
        val response = restClient.get().uri("/actuator/health/readiness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseBodyContent).isNotNull()

        val body = String(response.responseBodyContent)
        assertThat(body).contains("\"status\"")
    }

    @Test
    fun givenRunningApp_whenGetMetrics_thenReturnOk() {
        val response = restClient.get().uri("/actuator/metrics")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseBodyContent).isNotNull()

        val body = String(response.responseBodyContent)
        assertThat(body).contains("\"names\"")
    }

    @Test
    fun givenRunningApp_whenGetPrometheus_thenReturnMetrics() {
        val response = restClient.get().uri("/actuator/prometheus")
            .exchange()
            .returnResult()

        assertThat(response.status).isEqualTo(HttpStatus.OK)
        assertThat(response.responseBodyContent).isNotNull()

        val body = String(response.responseBodyContent)
        assertThat(body).contains("jvm_")
    }
}
