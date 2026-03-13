package io.github.malczuuu.lemur.app.domain.player

enum class PlayerStatus(val label: String) {
    ACTIVE("active"),
    BANNED("banned"),
    ;

    companion object {
        fun parse(status: String): PlayerStatus = entries.find { it.label == status }
            ?: throw IllegalArgumentException("Unknown player status: $status")
    }
}
