package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportResponse
import kotlin.time.Instant

data class PlayerDto(
    val id: String,
    val name: String,
    val rating: Int,
    val status: String,
    val createdDate: Instant?,
    val lastModifiedDate: Instant?,
    val version: Long,
) : TransportResponse
