package io.github.malczuuu.lemur.contract.rest;

import io.github.malczuuu.lemur.contract.TransportResponse;
import java.util.List;

public record ContentResult<T>(List<T> content) implements TransportResponse {}
