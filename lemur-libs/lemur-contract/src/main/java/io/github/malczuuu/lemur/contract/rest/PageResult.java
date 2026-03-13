package io.github.malczuuu.lemur.contract.rest;

import io.github.malczuuu.lemur.contract.TransportResponse;
import java.util.List;

public record PageResult<T>(
    List<T> content, Long page, Long size, Long totalElements, Long totalPages)
    implements TransportResponse {}
