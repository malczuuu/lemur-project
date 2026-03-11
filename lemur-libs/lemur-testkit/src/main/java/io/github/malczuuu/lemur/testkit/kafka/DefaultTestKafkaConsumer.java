package io.github.malczuuu.lemur.testkit.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * Test utility for consuming records from a Kafka topic in integration tests.
 *
 * <p>Wraps a {@link KafkaConsumer} and provides polling and buffer management methods.
 */
class DefaultTestKafkaConsumer implements TestKafkaConsumer {

  private final KafkaConsumer<String, String> consumer;

  /**
   * Creates a new {@link TestKafkaConsumer} and subscribes to the given topic.
   *
   * @param consumer the underlying KafkaConsumer
   * @param topic the topic to subscribe to
   */
  DefaultTestKafkaConsumer(KafkaConsumer<String, String> consumer, String topic) {
    this.consumer = consumer;
    this.consumer.subscribe(Collections.singletonList(topic));
  }

  /**
   * Polls the Kafka topic for records within the given timeout.
   *
   * @param timeout the maximum time to wait
   * @return an unmodifiable list of consumed records
   */
  @Override
  public List<ConsumerRecord<String, String>> poll(Duration timeout) {
    List<ConsumerRecord<String, String>> result = new ArrayList<>();
    ConsumerRecords<String, String> records = consumer.poll(timeout);
    records.forEach(result::add);
    return Collections.unmodifiableList(result);
  }

  /**
   * Drains all buffered records from the consumer, but will not run longer than the given timeout.
   *
   * @param timeout maximum duration to spend draining
   */
  @Override
  public void clear(Duration timeout) {
    long deadlineMillis = System.currentTimeMillis() + timeout.toMillis();
    while (System.currentTimeMillis() < deadlineMillis) {
      if (consumer.poll(Duration.ofMillis(100)).isEmpty()) {
        break;
      }
    }
  }

  /** Closes the underlying {@link KafkaConsumer}. */
  @Override
  public void close() {
    consumer.close();
  }
}
