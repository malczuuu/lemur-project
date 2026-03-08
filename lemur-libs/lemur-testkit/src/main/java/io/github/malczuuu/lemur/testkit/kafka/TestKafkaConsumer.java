package io.github.malczuuu.lemur.testkit.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class TestKafkaConsumer implements AutoCloseable {

  private final KafkaConsumer<String, String> consumer;

  TestKafkaConsumer(KafkaConsumer<String, String> consumer, String topic) {
    this.consumer = consumer;
    this.consumer.subscribe(Collections.singletonList(topic));
  }

  public List<ConsumerRecord<String, String>> poll(Duration timeout) {
    List<ConsumerRecord<String, String>> result = new ArrayList<>();
    ConsumerRecords<String, String> records = consumer.poll(timeout);
    records.forEach(result::add);
    return Collections.unmodifiableList(result);
  }

  public void clear() {
    while (!consumer.poll(Duration.ofMillis(100)).isEmpty()) {
      // drain all buffered records
    }
  }

  @Override
  public void close() {
    consumer.close();
  }
}
