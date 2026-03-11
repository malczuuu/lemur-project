package io.github.malczuuu.lemur.app.infra;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class CommonConfiguration {

  @Bean
  Clock clock() {
    return Clock.system(ZoneId.systemDefault());
  }
}
