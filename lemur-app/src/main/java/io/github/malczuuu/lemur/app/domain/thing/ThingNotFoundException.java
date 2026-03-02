package io.github.malczuuu.lemur.app.domain.thing;

public class ThingNotFoundException extends ThingException {

  public ThingNotFoundException() {
    super(ThingError.THING_NOT_FOUND);
  }
}
