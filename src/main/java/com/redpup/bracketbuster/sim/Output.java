package com.redpup.bracketbuster.sim;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Map.Entry.comparingByValue;
import static java.util.Objects.requireNonNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.util.Pair;
import com.redpup.bracketbuster.util.WeightedDoubleMetric;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Collection of output of a simulation.
 */
public final class Output {

  /**
   * Builds a {@link Output} of the given args.
   */
  public static Output buildOutput(
      Map<Lineup, WeightedDoubleMetric> lineupsByWinRateMetric, MatchupMatrix matchups,
      SortType sortType, int limit) {
    return new Output(
        limitAndCopyTopLineups(lineupsByWinRateMetric, sortType, limit),
        computeMetaCompPercentMap(lineupsByWinRateMetric, matchups));
  }

  /**
   * Computes a map of the top {@code limit} lineups by win rate and collects them into a map.
   *
   * <p>As part of this operation, invokes {@link Lineup#copy()} on all key lineups. This means
   * further mutations to metadata will not mutate this map.
   */
  @VisibleForTesting
  static ImmutableMap<Lineup, WeightedDoubleMetric> limitAndCopyTopLineups(
      Map<Lineup, WeightedDoubleMetric> lineupsByWinRate, SortType sortType, int limit) {
    return lineupsByWinRate
        .entrySet()
        .stream()
        .sorted(comparingByValue(sortType.comparator))
        .limit(limit)
        .collect(toImmutableMap(p -> p.getKey().copy(), Map.Entry::getValue));
  }

  /**
   * Computes a map of deck name to meta composition percent.
   *
   * <p>Meta composition percent is defined here as the % of times the deck shows up in lineups in
   * {@code lineupsByWinRate}. Because each {@link Lineup} contains multiple decks, the resulting
   * percentages should sum to (100% * PLAYER_DECK_COUNT == 300%.)
   *
   * <p>The result is sorted in descending order of meta composition.
   */
  @VisibleForTesting
  static ImmutableMap<String, Double> computeMetaCompPercentMap(
      Map<Lineup, ?> lineupsByWinRate, MatchupMatrix matchups) {
    // Count the number of times each deck is used across all lineups.
    Map<String, Integer> metaCompCount = new HashMap<>();
    for (String deck : matchups.getHeaders()) {
      metaCompCount.put(deck, 0);
    }
    lineupsByWinRate.keySet().stream()
        .flatMap(p -> p.getDeckNames().stream())
        .forEach(deck -> metaCompCount.compute(deck, (unused, c) -> requireNonNull(c) + 1));

    // Map values to meta composition size, sort by count descending.
    return metaCompCount.entrySet().stream()
        .map(e -> Pair.of(e.getKey(), (double) e.getValue() / lineupsByWinRate.size()))
        .sorted(Pair.<String>rightDoubleComparator().reversed())
        .collect(Pair.toImmutableMap());
  }

  public final ImmutableMap<Lineup, WeightedDoubleMetric> topLineups;
  public final ImmutableMap<String, Double> metaCompPercent;

  @VisibleForTesting
  Output(
      ImmutableMap<Lineup, WeightedDoubleMetric> topLineups,
      ImmutableMap<String, Double> metaCompPercent) {
    this.topLineups = topLineups;
    this.metaCompPercent = metaCompPercent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Output output = (Output) o;
    return Objects.equals(topLineups, output.topLineups) &&
        Objects.equals(metaCompPercent, output.metaCompPercent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(topLineups, metaCompPercent);
  }

  @Override
  public String toString() {
    return "Output{" +
        "topLineups=" + topLineups +
        ", metaCompPercent=" + metaCompPercent +
        '}';
  }
}
