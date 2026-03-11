package io.github.malczuuu.lemur.app.common;

/**
 * A wrapper for a long id that can be invalid. This is useful for parsing ids from strings, where
 * the string may not be a valid long.
 */
public final class IdAsLong {

  /**
   * Parses a string into an {@link IdAsLong}. If the string is not a valid {@code long}, the
   * returned {@link IdAsLong} will be invalid.
   *
   * @param s the string to parse
   * @return an {@link IdAsLong} representing the parsed {@code long}
   */
  public static IdAsLong parse(String s) {
    return NumberUtils.safeParseLong(s).map(IdAsLong::of).orElseGet(IdAsLong::invalid);
  }

  /**
   * Creates a new {@link IdAsLong} with the given long value. The returned {@link IdAsLong} will be
   * valid.
   *
   * @param value the long value to wrap
   * @return a new {@link IdAsLong} representing the given long value
   */
  public static IdAsLong of(long value) {
    return new IdAsLong(value);
  }

  /**
   * Returns an invalid {@link IdAsLong}. This can be used to represent an id that could not be
   * parsed from a string.
   *
   * @return an invalid {@link IdAsLong}
   */
  public static IdAsLong invalid() {
    return INVALID;
  }

  private static final IdAsLong INVALID = new IdAsLong(-1L, false);

  private final long id;
  private final boolean valid;

  private IdAsLong(long id) {
    this(id, true);
  }

  private IdAsLong(long id, boolean valid) {
    this.id = id;
    this.valid = valid;
  }

  /**
   * Returns the long id represented by this {@link IdAsLong}. If this {@link IdAsLong} is invalid,
   * an {@link IllegalStateException} will be thrown.
   *
   * @return the long id represented by this {@link IdAsLong}
   * @throws IllegalStateException if this {@link IdAsLong} is invalid
   */
  public long get() {
    if (!valid) {
      throw new IllegalStateException("id is not valid");
    }
    return id;
  }

  /**
   * Returns whether this {@link IdAsLong} is valid. An {@link IdAsLong} is valid if it was created
   * from a valid integer string, and invalid if it was created from an un-parseable string.
   *
   * @return {@code true} if this {@link IdAsLong} is valid, {@code false} otherwise
   */
  public boolean isValid() {
    return valid;
  }
}
