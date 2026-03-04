package io.github.malczuuu.lemur.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThingDto(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description) {}
