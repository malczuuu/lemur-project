package io.github.malczuuu.lemur.app.domain.error;

import org.jspecify.annotations.Nullable;

public interface ErrorAware {

  ErrorType getError();

  @Nullable String getDetail();
}
