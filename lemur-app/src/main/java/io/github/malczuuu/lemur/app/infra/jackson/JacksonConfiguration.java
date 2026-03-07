package io.github.malczuuu.lemur.app.infra.jackson;

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

  @Bean
  public JsonMapperBuilderCustomizer enumLabelMixInCustomizer() {
    return builder -> builder.addMixIn(PlayerStatus.class, EnumLabelMixIn.class);
  }
}
