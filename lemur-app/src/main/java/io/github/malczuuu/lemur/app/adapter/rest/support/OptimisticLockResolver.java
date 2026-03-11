package io.github.malczuuu.lemur.app.adapter.rest.support;

import io.github.malczuuu.lemur.app.domain.error.DomainError;
import io.github.problem4j.core.Problem;
import io.github.problem4j.core.ProblemBuilder;
import io.github.problem4j.core.ProblemContext;
import io.github.problem4j.spring.web.resolver.AbstractProblemResolver;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
public class OptimisticLockResolver extends AbstractProblemResolver {

  public OptimisticLockResolver() {
    super(OptimisticLockException.class);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder()
        .type(DomainError.CONCURRENT_MODIFICATION.getType())
        .status(HttpStatus.CONFLICT.value());
  }
}
