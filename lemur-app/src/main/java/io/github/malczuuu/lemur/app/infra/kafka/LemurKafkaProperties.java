package io.github.malczuuu.lemur.app.infra.kafka;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "lemur-app.kafka")
public class LemurKafkaProperties {

  private final Topic topic;

  public LemurKafkaProperties(@Nullable Topic topic) {
    this.topic = topic != null ? topic : Topic.defaultValue();
  }

  public Topic getTopic() {
    return topic;
  }

  public static class Topic {

    private static final String DEFAULT_PLAYER_EVENTS = "player-events";
    private static final String DEFAULT_FALLBACK_EVENTS = "fallback-events";

    /** The topic to which all player events are published. */
    private final String playerEvents;

    /** The topic to which all unrecognized events are published. */
    private final String fallbackEvents;

    public Topic(
        @DefaultValue(DEFAULT_PLAYER_EVENTS) String playerEvents,
        @DefaultValue(DEFAULT_FALLBACK_EVENTS) String fallbackEvents) {
      this.playerEvents = playerEvents;
      this.fallbackEvents = fallbackEvents;
    }

    /**
     * Returns topic to which all player events are published.
     *
     * @return the topic name for player events
     */
    public String getPlayerEvents() {
      return playerEvents;
    }

    /**
     * Returns topic to which all unrecognized events are published.
     *
     * @return the topic name for fallback events
     */
    public String getFallbackEvents() {
      return fallbackEvents;
    }

    /**
     * Returns a default Topic instance that is used if {@code topic} argument is {@code null}.
     *
     * @return a default Topic instance with predefined topic names for player events and fallback
     *     events
     */
    private static Topic defaultValue() {
      return new Topic(DEFAULT_PLAYER_EVENTS, DEFAULT_FALLBACK_EVENTS);
    }
  }
}
