package io.github.malczuuu.lemur.app;

import io.github.malczuuu.lemur.testkit.annotation.ContainerTest;
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest;
import io.github.malczuuu.lemur.testkit.container.PostgresAwareTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test"})
@ContainerTest
@SpringBootTest(classes = {LemurApplication.class})
class LemurApplicationTests implements KafkaAwareTest, PostgresAwareTest {

  @Test
  void contextLoads() {}
}
