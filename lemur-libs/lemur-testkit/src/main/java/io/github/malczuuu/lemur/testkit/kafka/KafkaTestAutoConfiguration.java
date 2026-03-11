package io.github.malczuuu.lemur.testkit.kafka;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

/**
 * Auto-configuration that waits for all Kafka listeners to be assigned before the application
 * context is fully refreshed. This ensures that any tests relying on Kafka listeners will not start
 * until the listeners are ready to receive messages, preventing race conditions and ensuring
 * reliable test execution.
 */
@AutoConfiguration
@ConditionalOnBean(KafkaListenerEndpointRegistry.class)
public final class KafkaTestAutoConfiguration {

  @Bean
  ApplicationListener<ContextRefreshedEvent> kafkaAssignmentAwaiter(
      KafkaListenerEndpointRegistry registry) {
    return _ -> KafkaTestUtils.awaitAssignment(registry);
  }
}
