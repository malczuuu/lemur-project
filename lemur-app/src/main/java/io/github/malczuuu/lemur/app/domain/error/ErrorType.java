package io.github.malczuuu.lemur.app.domain.error;

public interface ErrorType {

  ErrorCategory getCategory();

  String getType();
}
