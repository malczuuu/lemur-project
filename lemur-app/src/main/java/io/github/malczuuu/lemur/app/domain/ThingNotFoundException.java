package io.github.malczuuu.lemur.app.domain;

import io.github.malczuuu.lemur.app.common.NotFoundException;

public class ThingNotFoundException extends NotFoundException {

  public ThingNotFoundException() {
    super("thing not found");
  }
}
