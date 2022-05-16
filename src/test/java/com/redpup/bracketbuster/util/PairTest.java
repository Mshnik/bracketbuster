package com.redpup.bracketbuster.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import java.util.stream.Stream;
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

  @Test
  public void collectorCollectsPairs() {
    assertThat(Stream.of(Pair.of("A", 1), Pair.of("B", 2)).collect(Pair.toImmutableMap()))
        .containsExactly("A", 1, "B", 2);
  }

  @Test
  public void comparatorComparesByDoubles() {
    assertThat(Pair.rightDoubleComparator().compare(Pair.of(1, 1.0), Pair.of(2, 2.0)))
        .isLessThan(0);
    assertThat(Pair.rightDoubleComparator().compare(Pair.of(1, 2.0), Pair.of(2, 2.0)))
        .isEqualTo(0);
    assertThat(Pair.rightDoubleComparator().compare(Pair.of(1, 3.0), Pair.of(2, 2.0)))
        .isGreaterThan(0);
  }
}