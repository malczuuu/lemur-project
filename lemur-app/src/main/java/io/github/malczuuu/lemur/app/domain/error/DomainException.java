package io.github.malczuuu.lemur.app.domain.error;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class DomainException extends RuntimeException implements ErrorAware {

  private final ErrorType error;
  private final @Nullable String detail;

  public DomainException() {
    this(DomainError.INTERNAL_ERROR);
  }

  public DomainException(ErrorType error) {
    super(error.getType());
    this.error = error;
    this.detail = null;
  }

  public DomainException(ErrorType error, @Nullable String detail) {
    super(makeDefaultMessage(error, detail));
    this.error = error;
    this.detail = detail;
  }

  public DomainException(ErrorType error, @Nullable String detail, Throwable cause) {
    super(makeDefaultMessage(error, detail), cause);
    this.error = error;
    this.detail = detail;
  }

  public DomainException(ErrorType error, Throwable cause) {
    super(error.getType(), cause);
    this.error = error;
    this.detail = null;
  }

  protected DomainException(
      ErrorType error,
      @Nullable String detail,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(makeDefaultMessage(error, detail), cause, enableSuppression, writableStackTrace);
    this.error = error;
    this.detail = detail;
  }

  protected static String makeDefaultMessage(ErrorType error, @Nullable String detail) {
    return error.getType() + ": " + detail;
  }
}
