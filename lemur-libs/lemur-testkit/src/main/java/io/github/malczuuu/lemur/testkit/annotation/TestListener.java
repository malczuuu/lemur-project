package io.github.malczuuu.lemur.testkit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code TestKafkaConsumer} field with the Kafka topic it should consume from and allows
 * injecting it in test classes. Supported by field injection only.
 *
 * <p>Example usage in a test class:
 *
 * <pre>{@code
 * @TestListener("${lemur-app.kafka.topic.player-events}")
 * private TestKafkaConsumer kafkaConsumer;
 * }</pre>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestListener {

  /**
   * The topic name or a property placeholder, e.g. {@code "${my.topic}"}.
   *
   * @return the topic name or property placeholder
   */
  String value();
}
