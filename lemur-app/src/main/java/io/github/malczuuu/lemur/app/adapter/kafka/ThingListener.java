package io.github.malczuuu.lemur.app.adapter.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ThingListener {

  private static final Logger log = LoggerFactory.getLogger(ThingListener.class);

  @KafkaListener(topics = {"lemur-topic"})
  public void onThingMessage(ConsumerRecord<String, String> record) {
    log.atInfo()
        .addKeyValue("topic", record.topic())
        .addKeyValue("partition", record.partition())
        .addKeyValue("offset", record.offset())
        .addKeyValue("key", record.key())
        .addKeyValue("value", record.value())
        .log("Received message from Kafka");
  }
}
