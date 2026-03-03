package io.github.malczuuu.lemur.app.domain.error;

public enum DomainError implements ErrorType {
  INTERNAL_ERROR(ErrorCategory.INTERNAL_ERROR),
  CONCURRENT_MODIFICATION(ErrorCategory.CONFLICT),
  ;

  private final ErrorCategory category;

  DomainError(ErrorCategory category) {
    this.category = category;
  }

  @Override
  public ErrorCategory getCategory() {
    return category;
  }

  @Override
  public String getType() {
    return name();
  }
}
