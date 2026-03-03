package io.github.malczuuu.lemur.model.rest;

import jakarta.validation.constraints.NotNull;

public record ThingCreateDto(@NotNull String name, @NotNull String description) {}
