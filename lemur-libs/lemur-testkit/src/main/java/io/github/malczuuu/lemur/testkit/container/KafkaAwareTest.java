package io.github.malczuuu.lemur.testkit.container;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Interface to be implemented by tests that require a Kafka container. It provides a pre-configured
 * Kafka container with necessary environment variables set for testing purposes.
 */
public interface KafkaAwareTest {

  /**
   * <strong>Must not be accessed directly by test code.</strong> To be used by test framework only.
   */
  @Container @ServiceConnection
  KafkaContainer kafkaContainer =
      new KafkaContainer(DockerImageName.parse("apache/kafka:4.2.0"))
          .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
          .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
          .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1");
}
