package io.github.malczuuu.lemur.testkit.kafka;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@SpringBootApplication(
    exclude = {
      DataSourceAutoConfiguration.class,
      HibernateJpaAutoConfiguration.class,
      FlywayAutoConfiguration.class
    })
class TestKafkaApplication {

  static final String TOPIC = "testkit-test-topic";

  @Bean
  KafkaAdmin.NewTopics topics() {
    return new KafkaAdmin.NewTopics(TopicBuilder.name(TOPIC).partitions(1).replicas(1).build());
  }

  @KafkaListener(topics = TOPIC, groupId = "testkit-app")
  void consume(String message) {}
}
