package io.github.malczuuu.lemur.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Identity(@JsonProperty("id") String id) {}
