package io.github.malczuuu.lemur.app.domain.player;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum PlayerStatus {
  ACTIVE("active"),
  BANNED("banned"),
  ;

  private final String label;

  PlayerStatus(String label) {
    this.label = label;
  }

  public static PlayerStatus parse(String status) {
    return Optional.ofNullable(MappingHolder.LABEL_TO_STATUS.get(status))
        .orElseThrow(() -> new IllegalArgumentException(status + " is not valid"));
  }

  private static final class MappingHolder {
    private static final Map<String, PlayerStatus> LABEL_TO_STATUS =
        Arrays.stream(PlayerStatus.values())
            .collect(Collectors.toMap(PlayerStatus::getLabel, Function.identity()));
  }
}
