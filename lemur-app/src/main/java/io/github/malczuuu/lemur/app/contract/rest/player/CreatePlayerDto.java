package io.github.malczuuu.lemur.app.contract.rest.player;

import jakarta.validation.constraints.NotBlank;

public record CreatePlayerDto(@NotBlank String name) {}
