package io.github.malczuuu.lemur.app.infra.data.jpa;

import java.util.Optional;

class IdentityMapper {

  Optional<Long> safeParseId(String id) {
    try {
      return Optional.of(Long.parseLong(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
