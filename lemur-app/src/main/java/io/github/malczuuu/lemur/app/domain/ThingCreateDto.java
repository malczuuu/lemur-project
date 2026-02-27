package io.github.malczuuu.lemur.app.domain;

import jakarta.validation.constraints.NotNull;

public record ThingCreateDto(@NotNull String name, @NotNull String description) {}
