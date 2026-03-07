package io.github.malczuuu.lemur.app.core;

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;
import java.time.Instant;

public record PlayerModel(
    String id,
    String name,
    int rating,
    PlayerStatus status,
    Instant createdDate,
    Instant lastModifiedDate,
    Long version) {}
