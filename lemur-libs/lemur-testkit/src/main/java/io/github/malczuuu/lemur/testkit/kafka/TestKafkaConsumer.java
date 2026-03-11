package io.github.malczuuu.lemur.testkit.kafka;

import java.time.Duration;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Test utility for consuming records from a Kafka topic in integration tests.
 *
 * <p>Wraps a {@code KafkaConsumer} and provides polling and buffer management methods.
 *
 * @see org.apache.kafka.clients.consumer.KafkaConsumer
 */
public interface TestKafkaConsumer extends AutoCloseable {

  /**
   * Polls the Kafka topic for records within the given timeout.
   *
   * @param timeout the maximum time to wait
   * @return an unmodifiable list of consumed records
   */
  List<ConsumerRecord<String, String>> poll(Duration timeout);

  /** Drains all buffered records from the consumer, with a time guard of 300 milliseconds. */
  default void clear() {
    clear(Duration.ofMillis(300));
  }

  /**
   * Drains all buffered records from the consumer, but will not run longer than the given timeout.
   *
   * @param timeout maximum duration to spend draining
   */
  void clear(Duration timeout);

  /** Closes the underlying {@code KafkaConsumer}. */
  @Override
  void close();
}
