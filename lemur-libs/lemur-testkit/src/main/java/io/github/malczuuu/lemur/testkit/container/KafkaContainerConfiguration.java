package io.github.malczuuu.lemur.testkit.container;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class KafkaContainerConfiguration {

  @Bean
  @ServiceConnection
  public KafkaContainer kafkaContainer() {
    return new KafkaContainer(DockerImageName.parse("apache/kafka:4.2.0"))
        .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1");
  }

  @Bean
  public KafkaAdmin.NewTopics lemurAppTopics(
      @Value("${lemur-app.kafka.topic.thing-events}") String thingEventsTopic) {
    return new KafkaAdmin.NewTopics(
        TopicBuilder.name(thingEventsTopic).partitions(5).replicas(1).build());
  }
}
