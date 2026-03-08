package io.github.malczuuu.lemur.testkit.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test"})
@KafkaAwareTest
@SpringBootTest(classes = {TestKafkaApplication.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestKafkaConsumerTests {

  @Autowired private KafkaOperations<String, String> kafkaOperations;

  @TestListener(TestKafkaApplication.TOPIC_IN)
  private TestKafkaConsumer consumerIn;

  @TestListener(TestKafkaApplication.TOPIC_OUT)
  private TestKafkaConsumer consumerOut;

  @BeforeEach
  void beforeEach() {
    consumerIn.clear();
    consumerOut.clear();
  }

  @Test
  void givenPublishedMessage_whenPoll_thenReturnsRecord() {
    kafkaOperations.send(TestKafkaApplication.TOPIC_IN, "key-1", "value-1").join();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records =
                  consumerIn.poll(Duration.ofMillis(500));
              assertThat(records)
                  .anySatisfy(
                      r -> {
                        assertThat(r.key()).isEqualTo("key-1");
                        assertThat(r.value()).isEqualTo("value-1");
                      });
            });
  }

  @Test
  void givenPublishedMessages_whenClear_thenSubsequentPollReturnsEmpty() {
    kafkaOperations.send(TestKafkaApplication.TOPIC_IN, "key-2", "value-2").join();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(() -> assertThat(consumerIn.poll(Duration.ofMillis(500))).isNotEmpty());

    consumerIn.clear();

    assertThat(consumerIn.poll(Duration.ofMillis(300))).isEmpty();
  }

  @Test
  void givenPublishedMessageWithHeader_whenPoll_thenHeaderIsAccessible() {
    ProducerRecord<String, String> record =
        new ProducerRecord<>(TestKafkaApplication.TOPIC_IN, "key-3", "value-3");
    record.headers().add("event_type", "TestEvent".getBytes(StandardCharsets.UTF_8));
    kafkaOperations.send(record).join();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records =
                  consumerIn.poll(Duration.ofMillis(500));
              assertThat(records)
                  .anySatisfy(
                      r ->
                          assertThat(r.headers().lastHeader("event_type"))
                              .isNotNull()
                              .satisfies(
                                  h ->
                                      assertThat(new String(h.value(), StandardCharsets.UTF_8))
                                          .isEqualTo("TestEvent")));
            });
  }

  @Test
  void givenMessageOnTopicIn_whenForwarded_thenAppearsOnTopicOutOnly() {
    consumerIn.clear();
    consumerOut.clear();
    kafkaOperations.send(TestKafkaApplication.TOPIC_IN, "key-x", "msg-x").join();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(
            () -> {
              // Should appear on OUT, not on IN
              assertThat(consumerIn.poll(Duration.ofMillis(500))).isEmpty();
              assertThat(consumerOut.poll(Duration.ofMillis(500)))
                  .anySatisfy(r -> assertThat(r.value()).isEqualTo("forwarded-msg-x"));
            });
  }

  /**
   * Verifies that {@link TestKafkaConsumerContextCustomizerFactory} returns {@code null} when the
   * test class has no {@link TestListener}-annotated fields, meaning the customizer (and therefore
   * the {@link TestKafkaConsumer} bean registration) is skipped entirely for such classes.
   */
  @Test
  void givenNoTopicConsumerFields_whenCreateContextCustomizer_thenReturnsNull() {
    TestKafkaConsumerContextCustomizerFactory factory =
        new TestKafkaConsumerContextCustomizerFactory();
    assertThat(factory.createContextCustomizer(NoListenerClass.class, List.of())).isNull();
  }

  /**
   * Verifies that {@link TestKafkaConsumerContextCustomizerFactory} returns a non-null customizer
   * when the test class has at least one {@link TestListener}-annotated field, meaning a {@link
   * TestKafkaConsumer} bean will be registered for the declared topic.
   */
  @Test
  void givenTopicConsumerField_whenCreateContextCustomizer_thenReturnsCustomizer() {
    TestKafkaConsumerContextCustomizerFactory factory =
        new TestKafkaConsumerContextCustomizerFactory();
    assertThat(factory.createContextCustomizer(TestKafkaConsumerTests.class, List.of()))
        .isNotNull();
  }

  static class NoListenerClass {}
}
