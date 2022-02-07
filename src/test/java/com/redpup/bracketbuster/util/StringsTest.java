package com.redpup.bracketbuster.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class StringsTest {

  @Test
  public void sanitize_alreadyValid() {
    assertThat(Strings.sanitize("A/B (C/D)"))
        .isEqualTo("A/B (C/D)");
  }

  @Test
  public void sanitize_trimsExtraWhitespace() {
    assertThat(Strings.sanitize(" A / B   ( C / D ) "))
        .isEqualTo("A/B (C/D)");
  }

  @Test
  public void sanitize_sortsPrimaryComponent() {
    assertThat(Strings.sanitize("B/A (C/D)"))
        .isEqualTo("A/B (C/D)");
  }

  @Test
  public void sanitize_sortsParenComponent() {
    assertThat(Strings.sanitize("A/B (D/C)"))
        .isEqualTo("A/B (C/D)");
  }

}
