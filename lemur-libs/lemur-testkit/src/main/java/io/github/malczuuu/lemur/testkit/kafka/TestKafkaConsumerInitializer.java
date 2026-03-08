package io.github.malczuuu.lemur.testkit.kafka;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

/**
 * {@link ContextCustomizer} that directly registers {@link TestKafkaConsumer} bean definitions into
 * the test application context for each {@link
 * io.github.malczuuu.lemur.testkit.annotation.TestListener}-annotated field found on the test
 * class. Produced by {@link TestKafkaConsumerContextCustomizerFactory}.
 */
class TestKafkaConsumerInitializer implements ContextCustomizer {

  private final Class<?> testClass;

  TestKafkaConsumerInitializer(Class<?> testClass) {
    this.testClass = testClass;
  }

  @Override
  public void customizeContext(
      ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getBeanFactory();
    new TestKafkaConsumerRegistrar(testClass, context.getEnvironment(), context.getBeanFactory())
        .registerBeanDefinitions(registry);
  }

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

  @Override
  public int hashCode() {
    return testClass.hashCode();
  }
}
