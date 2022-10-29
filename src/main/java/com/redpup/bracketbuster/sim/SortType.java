package com.redpup.bracketbuster.sim;

import static java.util.Comparator.comparingDouble;

import com.redpup.bracketbuster.util.WeightedDoubleMetric;
import java.util.Comparator;

/**
 * How to sort lineups after a round of play.
 */
enum SortType {
  /**
   * Sort lineups by unweighted mean win rate, descending.
   */
  UNWEIGHTED_MEAN_WIN_RATE(comparingDouble(WeightedDoubleMetric::getUnweightedMean).reversed()),
  /**
   * Sort lineups by _weighted_ mean win rate, descending.
   */
  WEIGHTED_MEAN_WIN_RATE(comparingDouble(WeightedDoubleMetric::getWeightedMean).reversed()),
  /**
   * Sorts lineups by unweighted median win rate, descending.
   */
  UNWEIGHTED_MEDIAN_WIN_RATE(comparingDouble(WeightedDoubleMetric::getMedian).reversed());

  /**
   * How to compare {@link WeightedDoubleMetric}s. By default, sorts descending.
   */
  final Comparator<WeightedDoubleMetric> comparator;

  SortType(Comparator<WeightedDoubleMetric> comparator) {
    this.comparator = comparator;
  }
}
