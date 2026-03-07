package io.github.malczuuu.lemur.app.common;

import static io.github.malczuuu.lemur.app.common.NumberUtils.safeParseLong;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NumberUtilsTests {

  @Test
  void givenValidNumber_whenSafeParseLong_thenReturnsNonEmptyOptional() {
    assertThat(safeParseLong("42")).isPresent().hasValue(42L);
  }

  @Test
  void givenNegativeNumber_whenSafeParseLong_thenReturnsNonEmptyOptional() {
    assertThat(safeParseLong("-100")).isPresent().hasValue(-100L);
  }

  @Test
  void givenZero_whenSafeParseLong_thenReturnsNonEmptyOptional() {
    assertThat(safeParseLong("0")).isPresent().hasValue(0L);
  }

  @Test
  void givenMaxLong_whenSafeParseLong_thenReturnsNonEmptyOptional() {
    assertThat(safeParseLong(String.valueOf(Long.MAX_VALUE))).isPresent().hasValue(Long.MAX_VALUE);
  }

  @Test
  void givenMinLong_whenSafeParseLong_thenReturnsNonEmptyOptional() {
    assertThat(safeParseLong(String.valueOf(Long.MIN_VALUE))).isPresent().hasValue(Long.MIN_VALUE);
  }

  @Test
  void givenNull_whenSafeParseLong_thenReturnsEmpty() {
    assertThat(safeParseLong(null)).isEmpty();
  }

  @Test
  void givenEmptyString_whenSafeParseLong_thenReturnsEmpty() {
    assertThat(safeParseLong("")).isEmpty();
  }

  @Test
  void givenBlankString_whenSafeParseLong_thenReturnsEmpty() {
    assertThat(safeParseLong("   ")).isEmpty();
  }

  @Test
  void givenNonNumericString_whenSafeParseLong_thenReturnsEmpty() {
    assertThat(safeParseLong("abc")).isEmpty();
  }

  @Test
  void givenDecimalNumber_whenSafeParseLong_thenReturnsEmpty() {
    assertThat(safeParseLong("3.14")).isEmpty();
  }

  @Test
  void givenNumberExceedingLongRange_whenSafeParseLong_thenReturnsEmpty() {
    assertThat(safeParseLong("99999999999999999999999")).isEmpty();
  }

  @Test
  void givenLeadingWhitespace_whenSafeParseLong_thenReturnsEmpty() {
    assertThat(safeParseLong(" 42")).isEmpty();
  }
}
