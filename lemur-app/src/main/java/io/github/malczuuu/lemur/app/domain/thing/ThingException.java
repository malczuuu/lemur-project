package io.github.malczuuu.lemur.app.domain.thing;

import io.github.malczuuu.lemur.app.domain.error.DomainException;

public class ThingException extends DomainException {

  public ThingException() {}

  public ThingException(ThingError error) {
    super(error);
  }

  public ThingException(ThingError error, String detail) {
    super(error, detail);
  }

  public ThingException(ThingError error, String detail, Throwable cause) {
    super(error, detail, cause);
  }

  public ThingException(ThingError error, Throwable cause) {
    super(error, cause);
  }
}
