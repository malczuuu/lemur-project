package io.github.malczuuu.lemur.app.contract.rest.player;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record PlayerDto(
    String id,
    String name,
    Integer rating,
    String status,
    @Nullable Instant createdDate,
    @Nullable Instant lastModifiedDate,
    Long version) {}
