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
 * {@link ContextCustomizerFactory} that produces a {@link TestKafkaConsumerInitializer} for any
 * test class (or its enclosing hierarchy) that declares {@link
 * io.github.malczuuu.lemur.testkit.annotation.TestListener}-annotated fields.
 *
 * <p>Registered via {@code META-INF/spring.factories}.
 */
class TestKafkaConsumerContextCustomizerFactory implements ContextCustomizerFactory {

  @Override
  public @Nullable ContextCustomizer createContextCustomizer(
      Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
    if (collectTopicAnnotatedFieldTypes(testClass).isEmpty()) {
      return null;
    }
    return new TestKafkaConsumerInitializer(testClass);
  }

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
