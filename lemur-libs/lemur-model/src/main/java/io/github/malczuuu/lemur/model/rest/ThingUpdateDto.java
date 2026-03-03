package io.github.malczuuu.lemur.model.rest;

import jakarta.validation.constraints.NotNull;

public record ThingUpdateDto(@NotNull String name, @NotNull String description) {}
