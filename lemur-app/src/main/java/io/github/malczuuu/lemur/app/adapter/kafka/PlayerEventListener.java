package io.github.malczuuu.lemur.app.adapter.kafka;

import static io.github.malczuuu.lemur.app.common.message.MessageHeader.EVENT_TYPE_HEADER;

import io.github.malczuuu.lemur.app.common.IdAsLong;
import io.github.malczuuu.lemur.app.common.message.MessageHeader;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEventLogEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEventLogJpaRepository;
import java.time.Clock;
import java.time.Instant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PlayerEventListener {

  private static final Logger log = LoggerFactory.getLogger(PlayerEventListener.class);

  private final PlayerEventLogJpaRepository logRepository;
  private final JsonMapper jsonMapper;
  private final Clock clock;

  public PlayerEventListener(
      PlayerEventLogJpaRepository logRepository, JsonMapper jsonMapper, Clock clock) {
    this.logRepository = logRepository;
    this.jsonMapper = jsonMapper;
    this.clock = clock;
  }

  @KafkaListener(topics = {"${lemur-app.kafka.topic.player-events}"})
  @Transactional
  public void onPlayerEvent(ConsumerRecord<String, String> record) {
    try {
      String eventType = MessageHeader.findHeader(record, EVENT_TYPE_HEADER).orElse("unknown");
      JsonNode node = jsonMapper.readTree(record.value());
      String playerId = node.path("playerId").asString();

      IdAsLong idAsLong = IdAsLong.parse(playerId);
      if (!idAsLong.isValid()) {
        logInvalidId(record, eventType, playerId);
        return;
      }

      PlayerEventLogEntity eventLogEntity = new PlayerEventLogEntity();
      eventLogEntity.setPlayerId(idAsLong.get());
      eventLogEntity.setEventType(eventType);
      eventLogEntity.setPayload(record.value());
      eventLogEntity.setPublishedDate(Instant.ofEpochMilli(record.timestamp()));
      eventLogEntity.setReceivedDate(clock.instant());
      logRepository.save(eventLogEntity);

      logPlayerEvent(record, eventType, playerId);
    } catch (JacksonException e) {
      logJacksonException(record, e);
    }
  }

  private void logInvalidId(
      ConsumerRecord<String, String> record, String eventType, String playerId) {
    log.atWarn()
        .addKeyValue("topic", record.topic())
        .addKeyValue("partition", record.partition())
        .addKeyValue("offset", record.offset())
        .addKeyValue("eventType", eventType)
        .addKeyValue("playerId", playerId)
        .log("Ignoring player event with invalid playerId");
  }

  private void logPlayerEvent(
      ConsumerRecord<String, String> record, String eventType, String playerId) {
    log.atInfo()
        .addKeyValue("topic", record.topic())
        .addKeyValue("partition", record.partition())
        .addKeyValue("offset", record.offset())
        .addKeyValue("eventType", eventType)
        .addKeyValue("playerId", playerId)
        .log("Player event logged");
  }

  private void logJacksonException(ConsumerRecord<String, String> record, JacksonException e) {
    log.atError()
        .addKeyValue("topic", record.topic())
        .addKeyValue("partition", record.partition())
        .addKeyValue("offset", record.offset())
        .setCause(e)
        .log("Failed to deserialize player event from Kafka");
  }
}
