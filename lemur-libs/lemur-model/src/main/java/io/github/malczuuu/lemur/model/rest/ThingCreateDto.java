package io.github.malczuuu.lemur.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record ThingCreateDto(
    @JsonProperty("name") @NotNull String name,
    @JsonProperty("description") @NotNull String description) {}
