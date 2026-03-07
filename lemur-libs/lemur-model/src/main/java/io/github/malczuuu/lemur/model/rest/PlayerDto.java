package io.github.malczuuu.lemur.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record PlayerDto(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("rating") Integer rating,
    @JsonProperty("status") String status,
    @JsonProperty("createdDate") Instant createdDate,
    @JsonProperty("lastModifiedDate") Instant lastModifiedDate,
    @JsonProperty("version") Long version) {}
