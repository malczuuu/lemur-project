package io.github.malczuuu.lemur.app.common;

/**
 * A wrapper for a long id that can be invalid. This is useful for parsing ids from strings, where
 * the string may not be a valid long.
 */
public final class ParsedLong {

  /**
   * Parses a string into an {@link ParsedLong}. If the string is not a valid {@code long}, the
   * returned {@link ParsedLong} will be invalid.
   *
   * @param s the string to parse
   * @return an {@link ParsedLong} representing the parsed {@code long}
   */
  public static ParsedLong parse(String s) {
    return NumberUtils.safeParseLong(s).map(ParsedLong::of).orElseGet(ParsedLong::invalid);
  }

  /**
   * Creates a new {@link ParsedLong} with the given long value. The returned {@link ParsedLong}
   * will be valid.
   *
   * @param value the long value to wrap
   * @return a new {@link ParsedLong} representing the given long value
   */
  public static ParsedLong of(long value) {
    return new ParsedLong(value);
  }

  /**
   * Returns an invalid {@link ParsedLong}. This can be used to represent an id that could not be
   * parsed from a string.
   *
   * @return an invalid {@link ParsedLong}
   */
  public static ParsedLong invalid() {
    return INVALID;
  }

  private static final ParsedLong INVALID = new ParsedLong(-1L, false);

  private final long id;
  private final boolean valid;

  private ParsedLong(long id) {
    this(id, true);
  }

  private ParsedLong(long id, boolean valid) {
    this.id = id;
    this.valid = valid;
  }

  /**
   * Returns the long id represented by this {@link ParsedLong}. If this {@link ParsedLong} is
   * invalid, an {@link IllegalStateException} will be thrown.
   *
   * @return the long id represented by this {@link ParsedLong}
   * @throws IllegalStateException if this {@link ParsedLong} is invalid
   */
  public long get() {
    if (!valid) {
      throw new IllegalStateException("id is not valid");
    }
    return id;
  }

  /**
   * Returns whether this {@link ParsedLong} is valid. An {@link ParsedLong} is valid if it was
   * created from a valid integer string, and invalid if it was created from an un-parseable string.
   *
   * @return {@code true} if this {@link ParsedLong} is valid, {@code false} otherwise
   */
  public boolean isValid() {
    return valid;
  }
}
