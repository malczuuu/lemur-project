package io.github.malczuuu.lemur.contract.rest.player;

import io.github.malczuuu.lemur.contract.TransportResponse;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record PlayerDto(
    String id,
    String name,
    Integer rating,
    String status,
    @Nullable Instant createdDate,
    @Nullable Instant lastModifiedDate,
    Long version)
    implements TransportResponse {}
