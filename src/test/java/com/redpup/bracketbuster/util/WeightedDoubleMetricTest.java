package com.redpup.bracketbuster.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class WeightedDoubleMetricTest {

  private static final double ERROR = 1.0e-8;

  @Test
  public void empty() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().build();
    assertThat(metric.getUnweightedMean()).isEqualTo(0.0);
    assertThat(metric.getWeightedMean()).isEqualTo(0.0);
    assertThat(metric.getStdDev()).isEqualTo(0.0);
    assertThat(metric.getMedian()).isEqualTo(0.0);
  }

  @Test
  public void oneValue_unweighted() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5).build();
    assertThat(metric.getWeightedMean()).isEqualTo(2.5);
    assertThat(metric.getUnweightedMean()).isEqualTo(2.5);
    assertThat(metric.getStdDev()).isEqualTo(0.0);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void oneValue_weighted() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5, 0.5).build();
    assertThat(metric.getWeightedMean()).isEqualTo(2.5);
    assertThat(metric.getUnweightedMean()).isEqualTo(2.5);
    assertThat(metric.getStdDev()).isEqualTo(0.0);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void twoValues_same_unweighted() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5).add(2.5).build();
    assertThat(metric.getWeightedMean()).isEqualTo(2.5);
    assertThat(metric.getUnweightedMean()).isEqualTo(2.5);
    assertThat(metric.getStdDev()).isEqualTo(0.0);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void twoValues_same_weighted_sameWeights() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5, 0.5).add(2.5, 0.5)
        .build();
    assertThat(metric.getUnweightedMean()).isEqualTo(2.5);
    assertThat(metric.getWeightedMean()).isEqualTo(2.5);
    assertThat(metric.getStdDev()).isEqualTo(0.0);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void twoValues_same_weighted_differentWeights() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5, 0.75).add(2.5, 0.5)
        .build();
    assertThat(metric.getUnweightedMean()).isEqualTo(2.5);
    assertThat(metric.getWeightedMean()).isEqualTo(2.5);
    assertThat(metric.getStdDev()).isEqualTo(0.0);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void twoValues_different_unweighted() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5).add(3.5).build();
    assertThat(metric.getWeightedMean()).isEqualTo(3.0);
    assertThat(metric.getUnweightedMean()).isEqualTo(3.0);
    assertThat(metric.getStdDev()).isEqualTo(0.5);
    assertThat(metric.getMedian()).isEqualTo(3.0);
  }

  @Test
  public void twoValues_different_weighted_sameWeights() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5, 0.5).add(3.5, 0.5)
        .build();
    assertThat(metric.getUnweightedMean()).isEqualTo(3.0);
    assertThat(metric.getWeightedMean()).isEqualTo(3.0);
    assertThat(metric.getStdDev()).isEqualTo(0.5);
    assertThat(metric.getMedian()).isEqualTo(3.0);
  }

  @Test
  public void twoValues_different_weighted_differentWeights() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5, 0.75).add(3.5, 0.5)
        .build();
    assertThat(metric.getUnweightedMean()).isEqualTo(3.0);
    assertThat(metric.getWeightedMean()).isEqualTo(2.9);
    assertThat(metric.getStdDev()).isEqualTo(0.5);
    assertThat(metric.getMedian()).isEqualTo(3.0);
  }


  @Test
  public void threeValues_different_unweighted() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5).add(3.5).add(0.0).build();
    assertThat(metric.getWeightedMean()).isEqualTo(2.0);
    assertThat(metric.getUnweightedMean()).isEqualTo(2.0);
    assertThat(metric.getStdDev()).isWithin(ERROR).of(1.4719601443);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void threeValues_different_weighted_sameWeights() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5, 0.5).add(3.5, 0.5)
        .add(0.0, 0.5)
        .build();
    assertThat(metric.getUnweightedMean()).isEqualTo(2.0);
    assertThat(metric.getWeightedMean()).isEqualTo(2.0);
    assertThat(metric.getStdDev()).isWithin(ERROR).of(1.4719601443);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void threeValues_different_weighted_differentWeights() {
    WeightedDoubleMetric metric = WeightedDoubleMetric.builder().add(2.5, 0.75).add(3.5, 0.5)
        .add(0.0, 0.15)
        .build();
    assertThat(metric.getUnweightedMean()).isEqualTo(2.0);
    assertThat(metric.getWeightedMean()).isWithin(ERROR).of(2.5892857142);
    assertThat(metric.getStdDev()).isWithin(ERROR).of(1.4719601443);
    assertThat(metric.getMedian()).isEqualTo(2.5);
  }

  @Test
  public void obeysHashAndEquals() {
    new EqualsTester()
        .addEqualityGroup(new WeightedDoubleMetric(0.0, 0.0, 0.0, 0.0),
            new WeightedDoubleMetric(0.0, 0.0, 0.0, 0.0))
        .addEqualityGroup(new WeightedDoubleMetric(1.0, 0.0, 0.0, 0.0),
            new WeightedDoubleMetric(1.0, 0.0, 0.0, 0.0))
        .addEqualityGroup(new WeightedDoubleMetric(0.0, 1.0, 0.0, 0.0),
            new WeightedDoubleMetric(0.0, 1.0, 0.0, 0.0))
        .addEqualityGroup(new WeightedDoubleMetric(0.0, 0.0, 1.0, 0.0),
            new WeightedDoubleMetric(0.0, 0.0, 1.0, 0.0))
        .addEqualityGroup(new WeightedDoubleMetric(0.0, 0.0, 0.0, 1.0),
            new WeightedDoubleMetric(0.0, 0.0, 0.0, 1.0))
        .testEquals();
  }

}