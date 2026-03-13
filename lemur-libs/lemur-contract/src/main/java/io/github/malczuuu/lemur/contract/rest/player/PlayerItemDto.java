package io.github.malczuuu.lemur.contract.rest.player;

import io.github.malczuuu.lemur.contract.TransportResponse;

public record PlayerItemDto(String id, String name, Integer rating, String status)
    implements TransportResponse {}
