package io.github.malczuuu.lemur.app.infra.kafka;

import org.springframework.boot.kafka.autoconfigure.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfiguration {

  @Bean
  public DefaultKafkaProducerFactoryCustomizer kafkaProducerFactoryCustomizer() {
    return producerFactory -> producerFactory.setTransactionIdPrefix("lemur-tx-");
  }
}
