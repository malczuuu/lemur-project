package io.github.malczuuu.lemur.app.domain.error;

import org.jspecify.annotations.Nullable;

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

  public DomainException(ErrorType error, String detail) {
    super(error.getType() + ": " + detail);
    this.error = error;
    this.detail = detail;
  }

  public DomainException(ErrorType error, String detail, Throwable cause) {
    super(error.getType() + ": " + detail, cause);
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
      String detail,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(error.getType() + ": " + detail, cause, enableSuppression, writableStackTrace);
    this.error = error;
    this.detail = detail;
  }

  @Override
  public ErrorType getError() {
    return error;
  }

  @Override
  public @Nullable String getDetail() {
    return detail;
  }
}
