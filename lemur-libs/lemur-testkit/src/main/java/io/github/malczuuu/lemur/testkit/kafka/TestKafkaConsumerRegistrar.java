package io.github.malczuuu.lemur.testkit.kafka;

import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.core.env.Environment;

/**
 * Scans the test class hierarchy for {@link TestListener}-annotated fields and registers a {@link
 * TestKafkaConsumer} bean per unique topic, qualified with {@link TestListener}.
 */
class TestKafkaConsumerRegistrar {

  private final Class<?> testClass;
  private final Environment environment;
  private final BeanFactory beanFactory;

  /**
   * Creates a new registrar for the given test class, environment, and bean factory.
   *
   * @param testClass the test class
   * @param environment the Spring environment
   * @param beanFactory the Spring bean factory
   */
  TestKafkaConsumerRegistrar(Class<?> testClass, Environment environment, BeanFactory beanFactory) {
    this.testClass = testClass;
    this.environment = environment;
    this.beanFactory = beanFactory;
  }

  /**
   * Registers {@link TestKafkaConsumer} bean definitions for all unique topics found in the test
   * class hierarchy.
   *
   * @param registry the bean definition registry
   */
  void registerBeanDefinitions(BeanDefinitionRegistry registry) {
    Class<?> clazz = testClass;
    while (clazz != null && clazz != Object.class) {
      for (Field field : clazz.getDeclaredFields()) {
        TestListener annotation = field.getAnnotation(TestListener.class);
        if (annotation != null) {
          String rawValue = annotation.value();
          String resolvedTopic = environment.resolveRequiredPlaceholders(rawValue);
          String beanName = "testKafkaConsumer[" + rawValue + "]";
          if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(beanName, buildDefinition(rawValue, resolvedTopic));
          }
        }
      }
      clazz = clazz.getSuperclass();
    }
  }

  /**
   * Builds a {@link BeanDefinition} for a {@link TestKafkaConsumer} bean for the given topic, with
   * an instance supplier that creates a Kafka consumer connected to the appropriate bootstrap
   * servers and topic.
   *
   * @param rawValue the raw topic value from the {@link TestListener} annotation, which may contain
   *     property placeholders
   * @param resolvedTopic the resolved topic name after placeholder resolution
   * @return a {@link BeanDefinition} for a {@link TestKafkaConsumer} bean for the given topic
   */
  private BeanDefinition buildDefinition(String rawValue, String resolvedTopic) {
    RootBeanDefinition definition = new RootBeanDefinition(TestKafkaConsumer.class);
    definition.setInstanceSupplier(
        () -> {
          KafkaConnectionDetails connectionDetails =
              beanFactory.getBean(KafkaConnectionDetails.class);
          String bootstrapServers =
              String.join(",", connectionDetails.getConsumer().getBootstrapServers());
          return createClient(bootstrapServers, resolvedTopic);
        });
    definition.setDestroyMethodNames("close");
    definition.addQualifier(new AutowireCandidateQualifier(TestListener.class, rawValue));
    return definition;
  }

  /**
   * Creates a {@link TestKafkaConsumer} connected to the given bootstrap servers and topic.
   *
   * @param bootstrapServers the Kafka bootstrap servers to connect to
   * @param resolvedTopic the Kafka topic to consume from
   * @return a {@link TestKafkaConsumer} connected to the given bootstrap servers and topic
   */
  private TestKafkaConsumer createClient(String bootstrapServers, String resolvedTopic) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer." + resolvedTopic);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    KafkaConsumer<String, String> consumer =
        new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
    return new TestKafkaConsumer(consumer, resolvedTopic);
  }
}
