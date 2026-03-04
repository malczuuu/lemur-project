package io.github.malczuuu.lemur.model.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThingCreatedEvent(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description) {}
