package io.github.malczuuu.lemur.app.infra.kafka;

import static io.github.malczuuu.lemur.model.kafka.KafkaHeader.EVENT_TYPE_HEADER;

import io.github.malczuuu.lemur.app.domain.player.PlayerBanned;
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway;
import io.github.malczuuu.lemur.app.domain.player.PlayerRatingChanged;
import io.github.malczuuu.lemur.app.domain.player.PlayerRegistered;
import io.github.malczuuu.lemur.model.message.PlayerBannedEvent;
import io.github.malczuuu.lemur.model.message.PlayerRatingChangedEvent;
import io.github.malczuuu.lemur.model.message.PlayerRegisteredEvent;
import io.micrometer.core.instrument.MeterRegistry;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
class KafkaPlayerEventGateway implements PlayerEventGateway {

  private static final String PLAYER_REGISTERED_SUCCESS =
      "lemur.kafka.player.registered.publish.success";
  private static final String PLAYER_REGISTERED_FAILURE =
      "lemur.kafka.player.registered.publish.failure";
  private static final String PLAYER_BANNED_SUCCESS = "lemur.kafka.player.banned.publish.success";
  private static final String PLAYER_BANNED_FAILURE = "lemur.kafka.player.banned.publish.failure";
  private static final String PLAYER_RATING_CHANGED_SUCCESS =
      "lemur.kafka.player.rating_changed.publish.success";
  private static final String PLAYER_RATING_CHANGED_FAILURE =
      "lemur.kafka.player.rating_changed.publish.failure";

  private static final Logger log = LoggerFactory.getLogger(KafkaPlayerEventGateway.class);

  private final KafkaOperations<String, String> kafkaOperations;
  private final JsonMapper jsonMapper;
  private final MeterRegistry meterRegistry;
  private final String topic;

  KafkaPlayerEventGateway(
      KafkaOperations<String, String> kafkaOperations,
      JsonMapper jsonMapper,
      MeterRegistry meterRegistry,
      @Value("${lemur-app.kafka.topic.player-events}") String topic) {
    this.kafkaOperations = kafkaOperations;
    this.jsonMapper = jsonMapper;
    this.meterRegistry = meterRegistry;
    this.topic = topic;
  }

  @Override
  public void publish(PlayerRegistered event) {
    String eventTypeName = "PlayerRegistered";
    try {
      PlayerRegisteredEvent message = new PlayerRegisteredEvent(event.playerId());
      send(event.playerId(), eventTypeName, jsonMapper.writeValueAsString(message));
      meterRegistry.counter(PLAYER_REGISTERED_SUCCESS).increment();
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventTypeName, e);
      meterRegistry.counter(PLAYER_REGISTERED_FAILURE).increment();
    }
  }

  @Override
  public void publish(PlayerBanned event) {
    String eventTypeName = "PlayerBanned";
    try {
      PlayerBannedEvent message = new PlayerBannedEvent(event.playerId());
      send(event.playerId(), eventTypeName, jsonMapper.writeValueAsString(message));
      meterRegistry.counter(PLAYER_BANNED_SUCCESS).increment();
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventTypeName, e);
      meterRegistry.counter(PLAYER_BANNED_FAILURE).increment();
    }
  }

  @Override
  public void publish(PlayerRatingChanged event) {
    String eventTypeName = "PlayerRatingChanged";
    try {
      PlayerRatingChangedEvent message =
          new PlayerRatingChangedEvent(event.playerId(), event.oldRating(), event.newRating());
      send(event.playerId(), eventTypeName, jsonMapper.writeValueAsString(message));
      meterRegistry.counter(PLAYER_RATING_CHANGED_SUCCESS).increment();
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventTypeName, e);
      meterRegistry.counter(PLAYER_RATING_CHANGED_FAILURE).increment();
    }
  }

  private void send(String key, String eventTypeName, String value) throws Exception {
    List<Header> headers =
        List.of(
            new RecordHeader(EVENT_TYPE_HEADER, eventTypeName.getBytes(StandardCharsets.UTF_8)));
    ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, key, value, headers);
    kafkaOperations.send(record).get();
  }
}
