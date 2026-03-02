package io.github.malczuuu.lemur.app.adapter.rest.support;

import io.github.malczuuu.lemur.app.domain.error.DomainException;
import io.github.malczuuu.lemur.app.domain.error.ErrorCategory;
import io.github.problem4j.core.Problem;
import io.github.problem4j.core.ProblemBuilder;
import io.github.problem4j.core.ProblemContext;
import io.github.problem4j.spring.web.resolver.AbstractProblemResolver;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
public class DomainExceptionResolver extends AbstractProblemResolver {

  private static final Map<ErrorCategory, HttpStatus> categoryToStatusMapping =
      Map.ofEntries(
          Map.entry(ErrorCategory.MALFORMED_PAYLOAD, HttpStatus.BAD_REQUEST),
          Map.entry(ErrorCategory.NOT_FOUND, HttpStatus.NOT_FOUND),
          Map.entry(ErrorCategory.CONFLICT, HttpStatus.CONFLICT),
          Map.entry(ErrorCategory.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));

  public DomainExceptionResolver() {
    super(DomainException.class);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    DomainException e = (DomainException) ex;

    HttpStatus httpStatus =
        categoryToStatusMapping.getOrDefault(
            e.getError().getCategory(), HttpStatus.INTERNAL_SERVER_ERROR);

    return Problem.builder()
        .type(e.getError().getType())
        .title(httpStatus.getReasonPhrase())
        .status(httpStatus.value())
        .detail(e.getMessage());
  }
}
