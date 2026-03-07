package io.github.malczuuu.lemur.app.core;

import jakarta.validation.constraints.NotBlank;

public record RegisterPlayerModel(@NotBlank String name) {}
