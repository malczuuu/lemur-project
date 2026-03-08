package io.github.malczuuu.lemur.testkit.kafka;

import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import java.lang.reflect.Field;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * {@link TestExecutionListener} that injects {@link TestKafkaConsumer} instances into {@link
 * io.github.malczuuu.lemur.testkit.annotation.TestListener}-annotated fields on the test instance,
 * without requiring {@code @Autowired}.
 *
 * <p>Registered via {@code META-INF/spring.factories}.
 */
class TestKafkaConsumerInjector implements TestExecutionListener {

  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    Object testInstance = testContext.getTestInstance();
    ApplicationContext applicationContext = testContext.getApplicationContext();

    Class<?> clazz = testInstance.getClass();
    while (clazz != null && clazz != Object.class) {
      for (Field field : clazz.getDeclaredFields()) {
        TestListener annotation = field.getAnnotation(TestListener.class);
        if (annotation != null && field.getType().equals(TestKafkaConsumer.class)) {
          String beanName = "testKafkaConsumer[" + annotation.value() + "]";
          TestKafkaConsumer consumer = (TestKafkaConsumer) applicationContext.getBean(beanName);
          field.setAccessible(true);
          field.set(testInstance, consumer);
        }
      }
      clazz = clazz.getSuperclass();
    }
  }
}
