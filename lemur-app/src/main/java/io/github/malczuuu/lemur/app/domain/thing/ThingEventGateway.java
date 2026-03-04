package io.github.malczuuu.lemur.app.domain.thing;

import io.github.malczuuu.lemur.model.message.ThingCreatedEvent;
import io.github.malczuuu.lemur.model.message.ThingUpdatedEvent;

public interface ThingEventGateway {

  void publish(ThingCreatedEvent event);

  void publish(ThingUpdatedEvent event);
}
