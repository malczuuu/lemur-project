package io.github.malczuuu.lemur.app.common;

import io.github.problem4j.core.Problem;
import io.github.problem4j.core.ProblemException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ProblemException {

  public NotFoundException(String detail) {
    super(Problem.of(HttpStatus.NOT_FOUND.value(), detail));
  }
}
