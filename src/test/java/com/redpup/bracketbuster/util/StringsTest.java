package com.redpup.bracketbuster.util;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
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

  @Test
  public void sanitize_isStable() {
    assertThat(Strings.sanitize(Strings.sanitize("B/A (D/C)")))
        .isEqualTo("A/B (C/D)");
  }

  @Test
  public void allComponentsUnique_oneElm_true() {
    assertThat(Strings.allComponentsUnique(List.of("A/B (C/D)"))).isTrue();
    assertThat(Strings.allComponentsUnique(List.of(" A / B  ( C / D ) "))).isTrue();
  }

  @Test
  public void allComponentsUnique_oneElm_false() {
    assertThat(Strings.allComponentsUnique(List.of("A/A (C/D)"))).isFalse();
    assertThat(Strings.allComponentsUnique(List.of("A/A/B (C/D)"))).isFalse();
    assertThat(Strings.allComponentsUnique(List.of(" A / A  ( C / D ) "))).isFalse();
  }

  @Test
  public void allComponentsUnique_multipleElems_true() {
    assertThat(Strings.allComponentsUnique(List.of("A/B (C/D)", "D/E (F/G)"))).isTrue();
    assertThat(Strings.allComponentsUnique(List.of("A/B (C/D)", "D/E (C/G)"))).isTrue();
  }

  @Test
  public void allComponentsUnique_multipleElems_false() {
    assertThat(Strings.allComponentsUnique(List.of("A/B (C/D)", "A/E (F/G)"))).isFalse();
    assertThat(Strings.allComponentsUnique(List.of("A/B (C/D)", "E/E (F/G)"))).isFalse();
    assertThat(Strings.allComponentsUnique(List.of("A/B (C/D)", "F/G (C/D)"))).isFalse();
  }

}
