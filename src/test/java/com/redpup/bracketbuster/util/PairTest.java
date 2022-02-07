package com.redpup.bracketbuster.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PairTest {

  @Test
  public void storesValues() {
    assertThat(Pair.of(1, "Foo").first()).isEqualTo(1);
    assertThat(Pair.of(1, "Foo").second()).isEqualTo("Foo");
  }

  @Test
  public void obeysEquals() {
    new EqualsTester()
        .addEqualityGroup(Pair.of(1, "Foo"), Pair.of(1, "Foo"))
        .addEqualityGroup(Pair.of(1, 2), Pair.of(1, 2))
        .addEqualityGroup(Pair.of(2, 1), Pair.of(2, 1))
        .testEquals();
  }
}