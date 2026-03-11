package io.github.malczuuu.lemur.app.core;

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record PlayerModel(
    String id,
    String name,
    int rating,
    PlayerStatus status,
    @Nullable Instant createdDate,
    @Nullable Instant lastModifiedDate,
    Long version) {}
