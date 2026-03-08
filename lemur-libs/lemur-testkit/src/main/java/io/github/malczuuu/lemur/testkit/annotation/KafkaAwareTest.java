package io.github.malczuuu.lemur.testkit.annotation;

import io.github.malczuuu.lemur.testkit.TestkitTags;
import io.github.malczuuu.lemur.testkit.container.KafkaContainerConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;
import org.springframework.context.annotation.Import;

/**
 * Annotation to enable Kafka Testcontainers support for integration tests.
 *
 * <p>When applied to a test class, this annotation automatically starts a Kafka container and
 * configures the Spring context to use it. It also tags the test with {@code testcontainers}.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * @KafkaAwareTest
 * class MyKafkaIntegrationTest { ... }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Tag(TestkitTags.TESTCONTAINERS)
@Import(KafkaContainerConfiguration.class)
public @interface KafkaAwareTest {}
