package io.github.malczuuu.lemur.app.infra.jackson;

import com.fasterxml.jackson.annotation.JsonValue;

public interface EnumLabelMixIn {

  @JsonValue
  String getLabel();
}
