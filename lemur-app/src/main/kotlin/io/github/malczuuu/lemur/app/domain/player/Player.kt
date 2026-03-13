package io.github.malczuuu.lemur.app.domain.player

import io.github.malczuuu.lemur.app.domain.DomainId
import io.github.malczuuu.lemur.app.domain.DomainObject
import kotlin.math.max
import kotlin.time.Instant

data class Player(
    override val id: DomainId = DomainId.unassigned(),
    var name: String,
    var rating: Int = 0,
    var status: PlayerStatus = PlayerStatus.ACTIVE,
    val createdDate: Instant? = null,
    val lastModifiedDate: Instant? = null,
    var version: Long = 0L,
) : DomainObject {

    fun adjustRating(delta: Int) {
        this.rating = max(0, this.rating + delta)
    }

    fun ban() {
        if (status == PlayerStatus.BANNED) {
            throw PlayerAlreadyBannedException()
        }
        status = PlayerStatus.BANNED
    }

    fun unban() {
        if (status == PlayerStatus.ACTIVE) {
            throw PlayerNotBannedException()
        }
        status = PlayerStatus.ACTIVE
    }
}
