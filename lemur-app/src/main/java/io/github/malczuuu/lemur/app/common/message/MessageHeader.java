package io.github.malczuuu.lemur.app.common.message;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public final class MessageHeader {

  public static final String EVENT_TYPE_HEADER = "event_type";

  public static Optional<String> findHeader(ConsumerRecord<String, String> record, String key) {
    return Optional.ofNullable(record.headers().lastHeader(key))
        .map(header -> new String(header.value(), StandardCharsets.UTF_8));
  }

  private MessageHeader() {}
}
