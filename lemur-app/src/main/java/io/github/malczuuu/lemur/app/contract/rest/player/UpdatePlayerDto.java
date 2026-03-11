package io.github.malczuuu.lemur.app.contract.rest.player;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdatePlayerDto(@NotBlank String name, @NotNull @PositiveOrZero Long version) {}
