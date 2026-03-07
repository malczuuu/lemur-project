package io.github.malczuuu.lemur.app.adapter.kafka;

import static io.github.malczuuu.lemur.app.common.message.MessageHeader.EVENT_TYPE_HEADER;

import io.github.malczuuu.lemur.app.common.message.MessageHeader;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEventLogEntity;
import io.github.malczuuu.lemur.app.infra.data.jpa.PlayerEventLogJpaRepository;
import java.time.Clock;
import java.time.Instant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
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
  public void onPlayerEvent(ConsumerRecord<String, String> record) {
    try {
      String eventType = MessageHeader.findHeader(record, EVENT_TYPE_HEADER).orElse("unknown");
      JsonNode node = jsonMapper.readTree(record.value());
      String playerId = node.path("playerId").asString();

      PlayerEventLogEntity eventLogEntity = new PlayerEventLogEntity();
      eventLogEntity.setPlayerId(Long.parseLong(playerId));
      eventLogEntity.setEventType(eventType);
      eventLogEntity.setPayload(record.value());
      eventLogEntity.setPublishedDate(Instant.ofEpochMilli(record.timestamp()));
      eventLogEntity.setReceivedDate(clock.instant());
      logRepository.save(eventLogEntity);

      log.atInfo()
          .addKeyValue("topic", record.topic())
          .addKeyValue("partition", record.partition())
          .addKeyValue("offset", record.offset())
          .addKeyValue("eventType", eventType)
          .addKeyValue("playerId", playerId)
          .log("Player event logged");
    } catch (JacksonException e) {
      log.atError()
          .addKeyValue("topic", record.topic())
          .addKeyValue("partition", record.partition())
          .addKeyValue("offset", record.offset())
          .setCause(e)
          .log("Failed to deserialize player event from Kafka");
    }
  }
}
