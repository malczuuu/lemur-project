package io.github.malczuuu.lemur.app.infra.kafka;

import static io.github.malczuuu.lemur.contract.message.MessageHeader.EVENT_TYPE_HEADER;

import io.github.malczuuu.lemur.app.domain.player.PlayerEvent;
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway;
import io.github.malczuuu.lemur.contract.TransportMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerBannedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerCreatedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerRatingChangedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerUnbannedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerUpdatedMessage;
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

  private static final String PLAYER_EVENT_SUCCESS = "lemur.kafka.player.event.publish.success";
  private static final String PLAYER_EVENT_FAILURE = "lemur.kafka.player.event.publish.failure";
  private static final String EVENT_TYPE_TAG = "event_type";

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
  public void publish(PlayerEvent.PlayerCreated event) {
    String eventType = "PlayerCreated";
    try {
      PlayerCreatedMessage message = new PlayerCreatedMessage(event.playerId());
      send(event.playerId(), eventType, message);
      incrementSuccessMetric(eventType);
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventType, e);
      incrementFailureMetric(eventType);
      throw new KafkaGatewayException(makeExceptionMessage(eventType), e);
    }
  }

  @Override
  public void publish(PlayerEvent.PlayerUpdated event) {
    String eventType = "PlayerUpdated";
    try {
      PlayerUpdatedMessage message = new PlayerUpdatedMessage(event.playerId());
      send(event.playerId(), eventType, message);
      incrementSuccessMetric(eventType);
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventType, e);
      incrementFailureMetric(eventType);
      throw new KafkaGatewayException(makeExceptionMessage(eventType), e);
    }
  }

  @Override
  public void publish(PlayerEvent.PlayerBanned event) {
    String eventType = "PlayerBanned";
    try {
      PlayerBannedMessage message = new PlayerBannedMessage(event.playerId());
      send(event.playerId(), eventType, message);
      incrementSuccessMetric(eventType);
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventType, e);
      incrementFailureMetric(eventType);
      throw new KafkaGatewayException(makeExceptionMessage(eventType), e);
    }
  }

  @Override
  public void publish(PlayerEvent.PlayerUnbanned event) {
    String eventType = "PlayerUnbanned";
    try {
      PlayerUnbannedMessage message = new PlayerUnbannedMessage(event.playerId());
      send(event.playerId(), eventType, message);
      incrementSuccessMetric(eventType);
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventType, e);
      incrementFailureMetric(eventType);
      throw new KafkaGatewayException(makeExceptionMessage(eventType), e);
    }
  }

  @Override
  public void publish(PlayerEvent.PlayerRatingChanged event) {
    String eventType = "PlayerRatingChanged";
    try {
      PlayerRatingChangedMessage message =
          new PlayerRatingChangedMessage(event.playerId(), event.oldRating(), event.newRating());
      send(event.playerId(), eventType, message);
      incrementSuccessMetric(eventType);
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventType, e);
      incrementFailureMetric(eventType);
      throw new KafkaGatewayException(makeExceptionMessage(eventType), e);
    }
  }

  private void send(String key, String eventTypeName, TransportMessage message) {
    String value = jsonMapper.writeValueAsString(message);
    List<Header> headers = List.of(eventTypeHeader(eventTypeName));
    ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, key, value, headers);
    kafkaOperations.send(record).join();
  }

  private RecordHeader eventTypeHeader(String eventTypeName) {
    return new RecordHeader(EVENT_TYPE_HEADER, eventTypeName.getBytes(StandardCharsets.UTF_8));
  }

  private String makeExceptionMessage(String type) {
    return "Failed to publish " + type + " event to Kafka";
  }

  private void incrementSuccessMetric(String eventType) {
    meterRegistry.counter(PLAYER_EVENT_SUCCESS, EVENT_TYPE_TAG, eventType).increment();
  }

  private void incrementFailureMetric(String eventType) {
    meterRegistry.counter(PLAYER_EVENT_FAILURE, EVENT_TYPE_TAG, eventType).increment();
  }
}
