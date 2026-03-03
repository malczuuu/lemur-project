package io.github.malczuuu.lemur.app.adapter.kafka;

import io.github.malczuuu.lemur.model.kafka.ThingCreatedEvent;
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

  @KafkaListener(topics = {"lemur-topic"})
  public void onThingMessage(ConsumerRecord<String, String> record) {
    try {
      var event = jsonMapper.readValue(record.value(), ThingCreatedEvent.class);
      log.atInfo()
          .addKeyValue("topic", record.topic())
          .addKeyValue("partition", record.partition())
          .addKeyValue("offset", record.offset())
          .addKeyValue("key", record.key())
          .addKeyValue("thingId", event.id())
          .log("Received ThingCreatedEvent from Kafka");
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
}
