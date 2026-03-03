package io.github.malczuuu.lemur.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class LemurApplication {

  static void main(String[] args) {
    SpringApplication.run(LemurApplication.class, args);
  }
}
