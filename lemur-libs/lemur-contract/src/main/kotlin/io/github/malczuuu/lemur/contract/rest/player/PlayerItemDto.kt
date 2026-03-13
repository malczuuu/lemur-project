package io.github.malczuuu.lemur.contract.rest.player

import io.github.malczuuu.lemur.contract.TransportResponse

data class PlayerItemDto(val id: String, val name: String, val rating: Int, val status: String) : TransportResponse
