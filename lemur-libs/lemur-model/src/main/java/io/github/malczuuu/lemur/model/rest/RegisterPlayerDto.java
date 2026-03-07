package io.github.malczuuu.lemur.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RegisterPlayerDto(@JsonProperty("name") @NotBlank String name) {}
