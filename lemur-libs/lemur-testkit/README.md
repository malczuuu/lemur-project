# Lemur TestKit

A testing utility, containing a common setup for integration tests, such as Testcontainers configuration and shared test
utilities.

Documentation is available directly in-code as `JavaDoc` comments.

## Brief Overview

### **`@KafkaAwareTest`**

Annotation to add on test classes to enable Kafka Testcontainers setup.

```java
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;

@KafkaAwareTest
class MyKafkaTest {
    // Test code here
}
```

### **`@PostgresAwareTest`**

Annotation to add on test classes to enable PostgreSQL Testcontainers setup.

```java
import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;

@PostgresAwareTest
class MyPostgresTest {
  // Test code here
}
```

### **`@TestListener`**

Annotation to add on `TestKafkaConsumer` for automatic registration of the listener in tests.

```java
import io.github.malczuuu.lemur.testkit.annotation.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.annotation.TestListener;
import io.github.malczuuu.lemur.testkit.kafka.TestKafkaConsumer;

@KafkaAwareTest
class MyKafkaTest {

  @TestListener("${my.topic}")
  private TestKafkaConsumer consumer;

  // Test code here
}
```
