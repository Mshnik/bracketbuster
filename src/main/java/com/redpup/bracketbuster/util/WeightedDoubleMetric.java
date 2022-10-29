package com.redpup.bracketbuster.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * A tracker on a stream of weighted doubles, to compute various statistics on it.
 */
public final class WeightedDoubleMetric {

  private final double unweightedMean;
  private final double weightedMean;
  private final double stdDev;
  private final double median;

  @VisibleForTesting
  WeightedDoubleMetric(double unweightedMean, double weightedMean, double stdDev,
      double median) {
    this.unweightedMean = unweightedMean;
    this.weightedMean = weightedMean;
    this.stdDev = stdDev;
    this.median = median;
  }

  /**
   * Returns the unweighted mean of the metric.
   */
  public double getUnweightedMean() {
    return unweightedMean;
  }

  /**
   * Returns the weighted mean of the metric.
   */
  public double getWeightedMean() {
    return weightedMean;
  }

  /**
   * Returns the (unweighted) standard deviation of the metric.
   */
  public double getStdDev() {
    return stdDev;
  }

  /**
   * Returns the (unweighted) median of the metric.
   */
  public double getMedian() {
    return median;
  }

  /**
   * Returns a new {@link Builder}.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * A builder for {@link WeightedDoubleMetric}.
   */
  public static final class Builder {

    private double totalUnweightedValue;
    private double totalUnweightedValueSquared;

    private double totalWeightedValue;
    private double totalWeight;
    private int count;

    private final Queue<Double> minHeap;
    private final Queue<Double> maxHeap;

    private Builder() {
      minHeap = new PriorityQueue<>();
      maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    }

    /**
     * Adds the given {@code value} to this builder. If all values are added through this method
     * (instead of any through {@link #add(double, double)}), then all statistics are unweighted.
     */
    public Builder add(double value) {
      return add(value, 1);
    }

    /**
     * Adds the given {@code unweightedValue} with {@code weight} to this builder.
     */
    @CanIgnoreReturnValue
    public Builder add(double unweightedValue, double weight) {
      checkArgument(weight > 0);

      totalUnweightedValue += unweightedValue;
      totalUnweightedValueSquared += unweightedValue * unweightedValue;

      totalWeightedValue += unweightedValue * weight;
      totalWeight += weight;

      count++;

      if (minHeap.size() == maxHeap.size()) {
        maxHeap.offer(unweightedValue);
        minHeap.offer(maxHeap.poll());
      } else {
        minHeap.offer(unweightedValue);
        maxHeap.offer(minHeap.poll());
      }

      return this;
    }

    /**
     * Returns the unweighted mean of the values currently represented by this metric. (All weights
     * are ignored).
     */
    private double getUnweightedMean() {
      return totalUnweightedValue / count;
    }

    /**
     * Returns the weighted mean of the values currently represented by this metric.
     */
    private double getWeightedMean() {
      return totalWeightedValue / totalWeight;
    }

    /**
     * Returns the standard deviation of the values currently represented by this metric.
     */
    private double getStdDev() {
      double mean = getUnweightedMean();
      return Math.sqrt(totalUnweightedValueSquared / count - (mean * mean));
    }

    /**
     * Returns the median of the values currently represented by this metric.
     */
    private double getMedian() {
      double median;
      if (minHeap.size() > maxHeap.size()) {
        median = minHeap.peek();
      } else {
        median = (checkNotNull(minHeap.peek()) + checkNotNull(maxHeap.peek())) / 2.0;
      }
      return median;
    }

    /**
     * Builds this into a {@link WeightedDoubleMetric}.
     */
    public WeightedDoubleMetric build() {
      if (count == 0) {
        return new WeightedDoubleMetric(0, 0, 0, 0);
      } else {
        return new WeightedDoubleMetric(getUnweightedMean(), getWeightedMean(), getStdDev(),
            getMedian());
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WeightedDoubleMetric that = (WeightedDoubleMetric) o;
    return Double.compare(that.unweightedMean, unweightedMean) == 0 &&
        Double.compare(that.weightedMean, weightedMean) == 0 &&
        Double.compare(that.stdDev, stdDev) == 0 &&
        Double.compare(that.median, median) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(unweightedMean, weightedMean, stdDev, median);
  }

  @Override
  public String toString() {
    return "WeightedDoubleMetric{" +
        "unweightedMean=" + unweightedMean +
        ", weightedMean=" + weightedMean +
        ", stdDev=" + stdDev +
        ", median=" + median +
        '}';
  }
}
