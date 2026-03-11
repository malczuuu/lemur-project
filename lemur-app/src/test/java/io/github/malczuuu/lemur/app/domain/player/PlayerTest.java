package io.github.malczuuu.lemur.app.domain.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PlayerTest {

  @Test
  void ban_setsBannedToTrue() {
    Player player = new Player("Alice");

    player.ban();

    assertThat(player.getStatus()).isEqualTo(PlayerStatus.BANNED);
  }

  @Test
  void ban_whenAlreadyBanned_throws() {
    Player player = new Player("Alice");
    player.ban();

    assertThatThrownBy(player::ban).isInstanceOf(PlayerAlreadyBannedException.class);
  }

  @Test
  void adjustRating_withPositiveDelta_increasesRating() {
    Player player = new Player("Alice");
    int initial = player.getRating();

    player.adjustRating(50);

    assertThat(player.getRating()).isEqualTo(initial + 50);
  }

  @Test
  void adjustRating_withNegativeDeltaLargerThanRating_floorsAtZero() {
    Player player = new Player("Alice");
    player.adjustRating(1000);

    player.adjustRating(-9999);

    assertThat(player.getRating()).isZero();
  }

  @Test
  void adjustRating_withNegativeDelta_decreasesRating() {
    Player player = new Player("Alice");
    player.adjustRating(1000);

    int initial = player.getRating();

    player.adjustRating(-100);

    assertThat(player.getRating()).isEqualTo(initial - 100);
  }
}
