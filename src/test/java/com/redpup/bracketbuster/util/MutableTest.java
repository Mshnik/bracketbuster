package com.redpup.bracketbuster.util;


import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class MutableTest {

  @Test
  public void init_isNull() {
    assertThat(Mutable.create().get()).isNull();
  }

  @Test
  public void init_wrapsValue() {
    assertThat(Mutable.create(1).get()).isEqualTo(1);
  }

  @Test
  public void set() {
    Mutable<Integer> m = Mutable.create();
    m.set(1);
    assertThat(m.get()).isEqualTo(1);
  }

  @Test
  public void compute() {
    Mutable<Integer> m = Mutable.create(1);
    m.compute(i -> i + 1);
    assertThat(m.get()).isEqualTo(2);
  }

  @Test
  public void equalsAndHashcode() {
    new EqualsTester()
        .addEqualityGroup(Mutable.<String>create(), Mutable.<Integer>create())
        .addEqualityGroup(Mutable.create(1), Mutable.create().set(1), Mutable.create(2).set(1))
        .addEqualityGroup(Mutable.create(2), Mutable.create().set(2))
        .addEqualityGroup(Mutable.create("Hello"), Mutable.create().set("Hello"))
        .testEquals();
  }
}