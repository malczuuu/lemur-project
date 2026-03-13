package io.github.malczuuu.lemur.testkit.kafka;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

/**
 * Utility to wait until all Kafka listener containers have completed partition assignment.
 *
 * <p>Call this in {@code @BeforeAll} to prevent tests from running before the application's Kafka
 * consumer has rebalanced and is ready to receive messages.
 */
public final class KafkaTestUtils {

  private KafkaTestUtils() {}

  /**
   * Waits until all Kafka listener containers in the registry have completed partition assignment.
   *
   * @param registry the KafkaListenerEndpointRegistry to check
   */
  public static void awaitAssignment(KafkaListenerEndpointRegistry registry) {
    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(200))
        .until(() -> checkPartitionsAssignment(registry));
  }

  private static boolean checkPartitionsAssignment(KafkaListenerEndpointRegistry registry) {
    return registry.getAllListenerContainers().stream()
        .allMatch(KafkaTestUtils::checkPartitionsAssignment);
  }

  private static boolean checkPartitionsAssignment(MessageListenerContainer container) {
    var partitions = container.getAssignedPartitions();
    return container.isRunning() && partitions != null && !partitions.isEmpty();
  }
}
