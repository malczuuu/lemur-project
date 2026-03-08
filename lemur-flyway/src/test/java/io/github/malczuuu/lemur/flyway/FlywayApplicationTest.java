package io.github.malczuuu.lemur.flyway;

import io.github.malczuuu.lemur.testkit.annotation.PostgresAwareTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {FlywayApplication.class})
@PostgresAwareTest
class FlywayApplicationTest {

  @Test
  void contextLoads() {}
}
