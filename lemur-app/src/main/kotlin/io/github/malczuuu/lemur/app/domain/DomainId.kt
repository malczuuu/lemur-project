package io.github.malczuuu.lemur.app.domain

data class DomainId(val value: String) {

    constructor(value: Long) : this(value.toString())

    val assigned: Boolean = value.isNotEmpty()

    fun asLong(): Long? = value.toLongOrNull()

    companion object {
        fun unassigned() = DomainId("")
    }
}
