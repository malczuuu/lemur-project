package io.github.malczuuu.lemur.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThingCreatedEvent(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("version") Long version) {}
