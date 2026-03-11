package io.github.malczuuu.lemur.testkit.kafka;

import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

/**
 * {@link ContextCustomizer} that directly registers {@link TestKafkaConsumer} bean definitions into
 * the test application context for each {@link TestListener}-annotated field found on the test
 * class. Produced by {@link TestKafkaConsumerContextCustomizerFactory}.
 */
class TestKafkaConsumerInitializer implements ContextCustomizer {

  private final Class<?> testClass;

  /**
   * Creates a new initializer for the given test class.
   *
   * @param testClass the test class
   */
  TestKafkaConsumerInitializer(Class<?> testClass) {
    this.testClass = testClass;
  }

  /**
   * Customizes the application context by registering {@link TestKafkaConsumer} bean definitions
   * for each {@link TestListener}-annotated field found on the test class.
   *
   * @param context the context to customize
   * @param mergedConfig the merged context configuration
   */
  @Override
  public void customizeContext(
      ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getBeanFactory();
    new TestKafkaConsumerRegistrar(testClass, context.getEnvironment(), context.getBeanFactory())
        .registerBeanDefinitions(registry);
  }

  /**
   * Determines equality based on the test class, since the initializer is effectively a function of
   * the test class.
   *
   * @param obj the reference object with which to compare
   * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TestKafkaConsumerInitializer that)) {
      return false;
    }
    return testClass.equals(that.testClass);
  }

  /**
   * Returns a hash code based on the test class, since the initializer is effectively a function of
   * the test class.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return testClass.hashCode();
  }
}
