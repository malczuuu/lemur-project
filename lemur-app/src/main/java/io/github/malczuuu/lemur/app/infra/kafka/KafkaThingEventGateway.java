package io.github.malczuuu.lemur.app.infra.kafka;

import static io.github.malczuuu.lemur.model.kafka.KafkaHeader.EVENT_TYPE_HEADER;

import io.github.malczuuu.lemur.app.domain.thing.ThingEventGateway;
import io.github.malczuuu.lemur.model.message.ThingCreatedEvent;
import io.github.malczuuu.lemur.model.message.ThingUpdatedEvent;
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
class KafkaThingEventGateway implements ThingEventGateway {

  private static final String THING_CREATED_SUCCESS_METRIC =
      "lemur.kafka.thing.created.publish.success";
  private static final String THING_CREATED_FAILURE_METRIC =
      "lemur.kafka.thing.created.publish.failure";
  private static final String THING_UPDATED_SUCCESS_METRIC =
      "lemur.kafka.thing.updated.publish.success";
  private static final String THING_UPDATED_FAILURE_METRIC =
      "lemur.kafka.thing.updated.publish.failure";

  private static final Logger log = LoggerFactory.getLogger(KafkaThingEventGateway.class);

  private final KafkaOperations<String, String> kafkaOperations;
  private final JsonMapper jsonMapper;
  private final MeterRegistry meterRegistry;

  private final String topic;

  KafkaThingEventGateway(
      KafkaOperations<String, String> kafkaOperations,
      JsonMapper jsonMapper,
      MeterRegistry meterRegistry,
      @Value("${lemur-app.kafka.topic.thing-events}") String topic) {
    this.kafkaOperations = kafkaOperations;
    this.jsonMapper = jsonMapper;
    this.meterRegistry = meterRegistry;
    this.topic = topic;
  }

  @Override
  public void publish(ThingCreatedEvent event) {
    try {
      String key = event.id();
      String value = jsonMapper.writeValueAsString(event);
      List<Header> headers =
          List.of(
              new RecordHeader(
                  EVENT_TYPE_HEADER, "ThingCreatedEvent".getBytes(StandardCharsets.UTF_8)));

      ProducerRecord<String, String> record =
          new ProducerRecord<>(topic, null, key, value, headers);

      kafkaOperations.send(record).get();
      meterRegistry.counter(THING_CREATED_SUCCESS_METRIC).increment();
    } catch (Exception e) {
      log.error("Failed to publish ThingCreatedEvent to Kafka", e);
      meterRegistry.counter(THING_CREATED_FAILURE_METRIC).increment();
    }
  }

  @Override
  public void publish(ThingUpdatedEvent event) {
    try {
      String key = event.id();
      String value = jsonMapper.writeValueAsString(event);

      List<Header> headers =
          List.of(
              new RecordHeader(
                  EVENT_TYPE_HEADER, "ThingUpdatedEvent".getBytes(StandardCharsets.UTF_8)));

      ProducerRecord<String, String> record =
          new ProducerRecord<>(topic, null, key, value, headers);

      kafkaOperations.send(record).get();
      meterRegistry.counter(THING_UPDATED_SUCCESS_METRIC).increment();
    } catch (Exception e) {
      log.error("Failed to publish ThingUpdatedEvent to Kafka", e);
      meterRegistry.counter(THING_UPDATED_FAILURE_METRIC).increment();
    }
  }
}
