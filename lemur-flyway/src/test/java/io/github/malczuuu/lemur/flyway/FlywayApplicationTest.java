package io.github.malczuuu.lemur.flyway;

import io.github.malczuuu.lemur.testkit.annotation.ContainerTest;
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@ContainerTest
@SpringBootTest(classes = {FlywayApplication.class})
class FlywayApplicationTest implements PostgresAwareTest {

  @Test
  void contextLoads() {}
}
