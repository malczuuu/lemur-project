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

  @TestListener(TestKafkaApplication.TOPIC)
  private TestKafkaConsumer consumer;

  @BeforeEach
  void beforeEach() {
    consumer.clear();
  }

  @Test
  void givenPublishedMessage_whenPoll_thenReturnsRecord() {
    kafkaOperations.send(TestKafkaApplication.TOPIC, "key-1", "value-1").join();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records = consumer.poll(Duration.ofMillis(500));
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
    kafkaOperations.send(TestKafkaApplication.TOPIC, "key-2", "value-2").join();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(() -> assertThat(consumer.poll(Duration.ofMillis(500))).isNotEmpty());

    consumer.clear();

    assertThat(consumer.poll(Duration.ofMillis(300))).isEmpty();
  }

  @Test
  void givenPublishedMessageWithHeader_whenPoll_thenHeaderIsAccessible() {
    ProducerRecord<String, String> record =
        new ProducerRecord<>(TestKafkaApplication.TOPIC, "key-3", "value-3");
    record.headers().add("event_type", "TestEvent".getBytes(StandardCharsets.UTF_8));
    kafkaOperations.send(record).join();

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(
            () -> {
              List<ConsumerRecord<String, String>> records = consumer.poll(Duration.ofMillis(500));
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
