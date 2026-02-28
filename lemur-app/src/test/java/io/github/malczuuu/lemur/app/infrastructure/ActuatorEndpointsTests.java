package io.github.malczuuu.lemur.app.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.malczuuu.lemur.app.LemurApplication;
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

@ActiveProfiles(profiles = {"test"})
@PostgresAwareTest
@KafkaAwareTest
@SpringBootTest(
    classes = {LemurApplication.class})
@AutoConfigureMetrics
@AutoConfigureRestTestClient
class ActuatorEndpointsTests {

  @Autowired private RestTestClient restClient;

  @Test
  void givenRunningApp_whenGetHealth_thenReturnOk() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/actuator/health")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseBodyContent()).isNotNull();

    String body = new String(response.getResponseBodyContent());
    assertThat(body).contains("\"status\"");
  }

  @Test
  void givenRunningApp_whenGetLiveness_thenReturnOk() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/actuator/health/liveness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseBodyContent()).isNotNull();

    String body = new String(response.getResponseBodyContent());
    assertThat(body).contains("\"status\"");
  }

  @Test
  void givenRunningApp_whenGetReadiness_thenReturnOk() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/actuator/health/readiness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseBodyContent()).isNotNull();

    String body = new String(response.getResponseBodyContent());
    assertThat(body).contains("\"status\"");
  }

  @Test
  void givenRunningApp_whenGetMetrics_thenReturnOk() {
    ExchangeResult response =
        restClient
            .get()
            .uri("/actuator/metrics")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseBodyContent()).isNotNull();

    String body = new String(response.getResponseBodyContent());
    assertThat(body).contains("\"names\"");
  }

  @Test
  void givenRunningApp_whenGetPrometheus_thenReturnMetrics() {
    ExchangeResult response =
        restClient.get().uri("/actuator/prometheus").exchange().returnResult();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(response.getResponseBodyContent()).isNotNull();

    String body = new String(response.getResponseBodyContent());
    assertThat(body).contains("jvm_");
  }
}
