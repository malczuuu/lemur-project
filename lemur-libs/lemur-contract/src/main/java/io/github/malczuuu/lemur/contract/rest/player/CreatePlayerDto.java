package io.github.malczuuu.lemur.contract.rest.player;

import io.github.malczuuu.lemur.contract.TransportRequest;
import jakarta.validation.constraints.NotBlank;

public record CreatePlayerDto(@NotBlank String name) implements TransportRequest {}
