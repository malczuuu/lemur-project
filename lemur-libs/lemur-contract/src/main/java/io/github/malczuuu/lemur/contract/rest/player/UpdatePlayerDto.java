package io.github.malczuuu.lemur.contract.rest.player;

import io.github.malczuuu.lemur.contract.TransportRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdatePlayerDto(@NotBlank String name, @NotNull @PositiveOrZero Long version)
    implements TransportRequest {}
