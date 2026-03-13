package io.github.malczuuu.lemur.app.domain.player

import io.github.malczuuu.lemur.app.domain.error.ErrorCategory
import io.github.malczuuu.lemur.app.domain.error.ErrorType

enum class PlayerError(override val category: ErrorCategory) : ErrorType {
    PLAYER_NOT_FOUND(ErrorCategory.NOT_FOUND),
    PLAYER_ALREADY_BANNED(ErrorCategory.CONFLICT),
    PLAYER_NOT_BANNED(ErrorCategory.CONFLICT),
    ;

    override val type: String
        get() = name
}
