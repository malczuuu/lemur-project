package io.github.malczuuu.lemur.app.core.player;

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus;

public record PlayerItem(String id, String name, int rating, PlayerStatus status) {}
