package io.github.malczuuu.lemur.app.infra.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(classes = {JacksonAutoConfiguration.class, JacksonConfiguration.class})
class JacksonConfigurationTests {

  @Autowired private JsonMapper jsonMapper;

  @EnumSource(PlayerStatus.class)
  @ParameterizedTest
  void givenPlayerStatus_whenSerializing_thenShouldUseLabel(PlayerStatus status) {
    String json = jsonMapper.writeValueAsString(status);

    assertThat(json).isEqualTo("\"" + status.getLabel() + "\"");
  }

  @EnumSource(PlayerStatus.class)
  @ParameterizedTest
  void givenLabel_whenDeserializing_thenShouldParseToPlayerStatus(PlayerStatus status) {
    String json = "\"" + status.getLabel() + "\"";

    PlayerStatus deserializedStatus = jsonMapper.readValue(json, PlayerStatus.class);

    assertEquals(status, deserializedStatus);
  }

  @EnumSource(PlayerStatus.class)
  @ParameterizedTest
  void givenName_whenDeserializing_thenShouldFailParsing(PlayerStatus status) {
    String json = "\"" + status.name() + "\"";

    assertThatThrownBy(() -> jsonMapper.readValue(json, PlayerStatus.class))
        .isInstanceOf(InvalidFormatException.class)
        .satisfies(
            ex -> {
              InvalidFormatException e = (InvalidFormatException) ex;

              assertThat(e.getTargetType()).isEqualTo(PlayerStatus.class);
              assertThat(e.getValue()).isEqualTo(status.name());
            });
  }
}
