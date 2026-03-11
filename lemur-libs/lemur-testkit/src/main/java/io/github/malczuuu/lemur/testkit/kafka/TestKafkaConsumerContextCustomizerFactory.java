package io.github.malczuuu.lemur.testkit.kafka;

import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

/**
 * A {@link ContextCustomizerFactory} that produces a {@link TestKafkaConsumerInitializer} for any
 * test class (or its enclosing hierarchy) that declares {@link TestListener}-annotated fields.
 *
 * <p>Registered via {@code META-INF/spring.factories}.
 */
class TestKafkaConsumerContextCustomizerFactory implements ContextCustomizerFactory {

  /**
   * Creates a {@link ContextCustomizer} if the test class or any of its enclosing classes declare
   * {@link TestListener}-annotated fields.
   *
   * @param testClass the test class
   * @param configAttributes the list of context configuration attributes for the test class,
   *     ordered <em>bottom-up</em> (i.e., as if we were traversing up the class hierarchy or
   *     enclosing class hierarchy); never {@code null} or empty
   * @return a {@link ContextCustomizer} if the test class or any of its enclosing classes declare
   *     {@link TestListener}-annotated fields; otherwise, {@code null}
   */
  @Override
  public @Nullable ContextCustomizer createContextCustomizer(
      Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
    if (collectTopicAnnotatedFieldTypes(testClass).isEmpty()) {
      return null;
    }
    return new TestKafkaConsumerInitializer(testClass);
  }

  /**
   * Collects all topic values from {@link TestListener}-annotated fields in the test class
   * hierarchy.
   *
   * @param testClass the test class
   * @return a set of topic names or property placeholders
   */
  private Set<String> collectTopicAnnotatedFieldTypes(Class<?> testClass) {
    Set<String> raw = new LinkedHashSet<>();
    Class<?> clazz = testClass;
    while (clazz != null && clazz != Object.class) {
      Arrays.stream(clazz.getDeclaredFields())
          .filter(f -> f.isAnnotationPresent(TestListener.class))
          .map(f -> f.getAnnotation(TestListener.class).value())
          .forEach(raw::add);
      clazz = clazz.getSuperclass();
    }
    return raw;
  }
}
