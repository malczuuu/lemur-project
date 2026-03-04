package io.github.malczuuu.lemur.app.adapter.kafka;

import static io.github.malczuuu.lemur.model.kafka.KafkaHeader.EVENT_TYPE_HEADER;

import io.github.malczuuu.lemur.model.kafka.KafkaHeader;
import io.github.malczuuu.lemur.model.message.ThingCreatedEvent;
import io.github.malczuuu.lemur.model.message.ThingUpdatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ThingListener {

  private static final Logger log = LoggerFactory.getLogger(ThingListener.class);

  private final JsonMapper jsonMapper;

  public ThingListener(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @KafkaListener(topics = {"${lemur-app.kafka.topic.thing-events}"})
  public void onThingMessage(ConsumerRecord<String, String> record) {
    try {
      KafkaHeader.findHeader(record, EVENT_TYPE_HEADER)
          .ifPresent(
              eventType -> {
                switch (eventType) {
                  case "ThingCreatedEvent" -> onThingCreated(record);
                  case "ThingUpdatedEvent" -> onThingUpdated(record);
                }
              });
    } catch (JacksonException e) {
      log.atError()
          .addKeyValue("topic", record.topic())
          .addKeyValue("partition", record.partition())
          .addKeyValue("offset", record.offset())
          .addKeyValue("key", record.key())
          .setCause(e)
          .log("Failed to deserialize message from Kafka");
    }
  }

  private void onThingCreated(ConsumerRecord<String, String> record) {
    var event = jsonMapper.readValue(record.value(), ThingCreatedEvent.class);
    logEvent(record, event.id(), event.getClass().getSimpleName());
  }

  private void onThingUpdated(ConsumerRecord<String, String> record) {
    var event = jsonMapper.readValue(record.value(), ThingUpdatedEvent.class);
    logEvent(record, event.id(), event.getClass().getSimpleName());
  }

  private void logEvent(ConsumerRecord<String, String> record, String thingId, String className) {
    log.atInfo()
        .addKeyValue("topic", record.topic())
        .addKeyValue("partition", record.partition())
        .addKeyValue("offset", record.offset())
        .addKeyValue("key", record.key())
        .addKeyValue("thingId", thingId)
        .log("Received {} from Kafka", className);
  }
}
