package io.github.malczuuu.lemur.app.infra.kafka;

import io.github.malczuuu.lemur.app.domain.player.PlayerEvent;
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway;
import io.github.malczuuu.lemur.contract.message.player.PlayerBannedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerCreatedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerRatingChangedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerUnbannedMessage;
import io.github.malczuuu.lemur.contract.message.player.PlayerUpdatedMessage;
import io.micrometer.core.instrument.MeterRegistry;
import io.namastack.outbox.Outbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class KafkaPlayerEventGateway implements PlayerEventGateway {

  private static final String PLAYER_EVENT_SUCCESS = "lemur.kafka.player.event.publish.success";
  private static final String PLAYER_EVENT_FAILURE = "lemur.kafka.player.event.publish.failure";
  private static final String EVENT_TYPE_TAG = "event_type";

  private static final Logger log = LoggerFactory.getLogger(KafkaPlayerEventGateway.class);

  private final Outbox outbox;
  private final MeterRegistry meterRegistry;

  KafkaPlayerEventGateway(Outbox outbox, MeterRegistry meterRegistry) {
    this.outbox = outbox;
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void publish(PlayerEvent.PlayerCreated event) {
    String eventType = "PlayerCreated";
    try {
      PlayerCreatedMessage message = new PlayerCreatedMessage(event.playerId());
      outbox.schedule(message);
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
      outbox.schedule(message);
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
      outbox.schedule(message);
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
      outbox.schedule(message);
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
      outbox.schedule(message);
      incrementSuccessMetric(eventType);
    } catch (Exception e) {
      log.error("Failed to publish {} to Kafka", eventType, e);
      incrementFailureMetric(eventType);
      throw new KafkaGatewayException(makeExceptionMessage(eventType), e);
    }
  }

  private String makeExceptionMessage(String type) {
    return "Failed to publish " + type + " event to Outbox";
  }

  private void incrementSuccessMetric(String eventType) {
    meterRegistry.counter(PLAYER_EVENT_SUCCESS, EVENT_TYPE_TAG, eventType).increment();
  }

  private void incrementFailureMetric(String eventType) {
    meterRegistry.counter(PLAYER_EVENT_FAILURE, EVENT_TYPE_TAG, eventType).increment();
  }
}
