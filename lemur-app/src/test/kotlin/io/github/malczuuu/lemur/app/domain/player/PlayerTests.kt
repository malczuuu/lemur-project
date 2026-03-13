package io.github.malczuuu.lemur.app.domain.player

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import kotlin.test.Test

class PlayerTests {

    @Test
    fun ban_setsBannedToTrue() {
        val player = Player(name = "Alice")

        player.ban()

        assertThat(player.status).isEqualTo(PlayerStatus.BANNED)
    }

    @Test
    fun ban_whenAlreadyBanned_throws() {
        val player = Player(name = "Alice")
        player.ban()

        assertThatThrownBy { player.ban() }.isInstanceOf(PlayerAlreadyBannedException::class.java)
    }

    @Test
    fun adjustRating_withPositiveDelta_increasesRating() {
        val player = Player(name = "Alice")
        val initial = player.rating

        player.adjustRating(50)

        assertThat(player.rating).isEqualTo(initial + 50)
    }

    @Test
    fun adjustRating_withNegativeDeltaLargerThanRating_floorsAtZero() {
        val player = Player(name = "Alice")
        player.adjustRating(1000)

        player.adjustRating(-9999)

        assertThat(player.rating).isZero()
    }

    @Test
    fun adjustRating_withNegativeDelta_decreasesRating() {
        val player = Player(name = "Alice")
        player.adjustRating(1000)

        val initial = player.rating

        player.adjustRating(-100)

        assertThat(player.rating).isEqualTo(initial - 100)
    }
}
