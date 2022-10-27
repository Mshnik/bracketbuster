package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LineupWeightTypeTest {

  private static final double ERROR = 1.0e-8;

  @Test
  public void average_simple() {
    assertThat(LineupWeightType.AVERAGE.collect(DoubleStream.of(1.0, 1.0, 1.0)))
        .isEqualTo(1.0);
    assertThat(LineupWeightType.AVERAGE.collect(DoubleStream.of(1.0, 2.0, 3.0)))
        .isEqualTo(2.0);
    assertThat(LineupWeightType.AVERAGE.collect(DoubleStream.of(1.0, 2.0, 6.0)))
        .isEqualTo(3.0);
  }

  @Test
  public void average_boxed() {
    assertThat(Stream.of(1.0, 1.0, 1.0).collect(LineupWeightType.AVERAGE.collector()))
        .isEqualTo(1.0);
    assertThat(Stream.of(1.0, 2.0, 3.0).collect(LineupWeightType.AVERAGE.collector()))
        .isEqualTo(2.0);
    assertThat(Stream.of(1.0, 2.0, 6.0).collect(LineupWeightType.AVERAGE.collector()))
        .isEqualTo(3.0);
  }

  @Test
  public void geometric_simple() {
    assertThat(LineupWeightType.GEOMETRIC.collect(DoubleStream.of(1.0, 1.0, 1.0)))
        .isEqualTo(1.0);
    assertThat(LineupWeightType.GEOMETRIC.collect(DoubleStream.of(1.0, 2.0, 3.0)))
        .isWithin(ERROR)
        .of(Math.pow(6.0, 1.0 / 3.0));
    assertThat(LineupWeightType.GEOMETRIC.collect(DoubleStream.of(1.0, 2.0, 6.0)))
        .isWithin(ERROR)
        .of(Math.pow(12.0, 1.0 / 3.0));
  }

  @Test
  public void geometric_boxed() {
    assertThat(Stream.of(1.0, 1.0, 1.0).collect(LineupWeightType.GEOMETRIC.collector()))
        .isEqualTo(1.0);
    assertThat(Stream.of(1.0, 2.0, 3.0).collect(LineupWeightType.GEOMETRIC.collector()))
        .isWithin(ERROR)
        .of(Math.pow(6.0, 1.0 / 3.0));
    assertThat(Stream.of(1.0, 2.0, 6.0).collect(LineupWeightType.GEOMETRIC.collector()))
        .isWithin(ERROR)
        .of(Math.pow(12.0, 1.0 / 3.0));
  }
}