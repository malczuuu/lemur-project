package io.github.malczuuu.lemur.app.infra.kafka;

import static io.github.malczuuu.lemur.contract.message.MessageHeader.EVENT_TYPE_HEADER;

import io.github.malczuuu.lemur.contract.message.player.PlayerMessage;
import io.namastack.outbox.kafka.KafkaOutboxRouting;
import io.namastack.outbox.routing.OutboxRoute;
import io.namastack.outbox.routing.selector.OutboxPayloadSelector;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableConfigurationProperties(LemurKafkaProperties.class)
@EnableKafka
public class KafkaConfiguration {

  private final LemurKafkaProperties properties;

  public KafkaConfiguration(LemurKafkaProperties properties) {
    this.properties = properties;
  }

  @Bean
  KafkaOutboxRouting kafkaOutboxRouting(JsonMapper jsonMapper) {
    return KafkaOutboxRouting.builder()
        .route(
            OutboxPayloadSelector.type(PlayerMessage.class),
            builder -> {
              builder.target(properties.getTopic().getPlayerEvents());
              builder.key((payload, _) -> ((PlayerMessage) payload).playerId());
              builder.mapping((payload, _) -> jsonMapper.writeValueAsString(payload));
              builder.headers(
                  (payload, _) ->
                      Map.of(EVENT_TYPE_HEADER, ((PlayerMessage) payload).messageType()));
            })
        .defaults(
            (Consumer<OutboxRoute.Builder>)
                route -> route.target(properties.getTopic().getFallbackEvents()))
        .build();
  }
}
