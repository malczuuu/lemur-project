package io.github.malczuuu.lemur.app.domain.player

import io.github.malczuuu.lemur.app.domain.error.DomainException

open class PlayerException(error: PlayerError) : DomainException(error)
