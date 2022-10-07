package com.redpup.bracketbuster.model;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.redpup.bracketbuster.util.Constants.NUM_BEST_WORST_MATCHUPS;
import static com.redpup.bracketbuster.util.Pair.rightDoubleComparator;
import static java.util.Comparator.comparingDouble;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.redpup.bracketbuster.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Mutable container for metadata of a given lineup.
 */
public final class LineupMetadata {

  private static final double BAN_ERROR = 1.0e-8;

  private final int[] playedAgainst;
  private final double[] banned;

  private final PriorityQueue<Pair<Lineup, Double>> bestMatchups;
  private final PriorityQueue<Pair<Lineup, Double>> worstMatchups;

  LineupMetadata(int numDecks) {
    this(new int[numDecks],
        new double[numDecks],
        // Comparators are opposite the intended order so the "least good"
        // element is the first to be polled when necessary.
        // When reading the order should be reversed.
        new PriorityQueue<>(rightDoubleComparator()),
        new PriorityQueue<>(Pair.<Lineup>rightDoubleComparator().reversed()));
  }

  private LineupMetadata(int[] playedAgainst, double[] banned,
      PriorityQueue<Pair<Lineup, Double>> bestMatchups,
      PriorityQueue<Pair<Lineup, Double>> worstMatchups) {
    this.playedAgainst = playedAgainst;
    this.banned = banned;
    this.bestMatchups = bestMatchups;
    this.worstMatchups = worstMatchups;
  }

  /**
   * Returns {@link #playedAgainst}. The result is defensively copied; mutations will not be
   * reflected in this metadata.
   */
  public int[] getPlayedAgainst() {
    return Arrays.copyOf(playedAgainst, playedAgainst.length);
  }

  /**
   * Increments the count of playing against {@code deck}. Returns self.
   */
  @CanIgnoreReturnValue
  public LineupMetadata incrementPlayedAgainst(int deck) {
    playedAgainst[deck]++;
    return this;
  }

  /**
   * Returns {@link #banned}. The result is defensively copied; mutations will not be reflected in
   * this metadata.
   */
  public double[] getBanned() {
    return Arrays.copyOf(banned, banned.length);
  }

  /**
   * Increments the count of banning {@code deck}.
   */
  @CanIgnoreReturnValue
  public LineupMetadata incrementBanned(int deck) {
    banned[deck]++;
    return this;
  }


  /**
   * Increments the count of banning {@code deck} by {@code amount}.
   */
  @CanIgnoreReturnValue
  public LineupMetadata incrementBanned(int deck, double amount) {
    banned[deck] += roundBanAmount(amount);
    return this;
  }

  /**
   * Rounds {@code value} to be within one {@link #BAN_ERROR} of sig figs.
   */
  private static double roundBanAmount(double value) {
    return Math.round(value / BAN_ERROR) * BAN_ERROR;
  }

  /**
   * Returns {@link #bestMatchups}, in descending order of goodness. (Best first.)
   */
  public ImmutableMap<Lineup, Double> getBestMatchups() {
    return bestMatchups.stream()
        .sorted(bestMatchups.comparator().reversed())
        .collect(Pair.toImmutableMap());
  }

  /**
   * Returns {@link #worstMatchups}, in descending order of badness. (Worst first.)
   */
  public ImmutableMap<Lineup, Double> getWorstMatchups() {
    return worstMatchups.stream()
        .sorted(worstMatchups.comparator().reversed())
        .collect(Pair.toImmutableMap());
  }

  /**
   * Applies the given {@code opponent} lineup with computed {@code winRate} to this metadata.
   * Returns self.
   */
  @CanIgnoreReturnValue
  public LineupMetadata applyMatchup(Lineup opponent, double winRate) {
    bestMatchups.add(Pair.of(opponent, winRate));
    worstMatchups.add(Pair.of(opponent, winRate));

    if (bestMatchups.size() > NUM_BEST_WORST_MATCHUPS) {
      bestMatchups.poll();
    }
    if (worstMatchups.size() > NUM_BEST_WORST_MATCHUPS) {
      worstMatchups.poll();
    }

    return this;
  }

  /**
   * Resets this LineupMetadata, clearing all data. Returns self.
   */
  @CanIgnoreReturnValue
  public LineupMetadata reset() {
    Arrays.fill(playedAgainst, 0);
    Arrays.fill(banned, 0);
    bestMatchups.clear();
    worstMatchups.clear();
    return this;
  }

  /**
   * Copies this metadata. This and the copy will have equivalent but separate state, so mutations
   * to this will not affect copy and vice-versa.
   */
  public LineupMetadata copy() {
    return new LineupMetadata(
        getPlayedAgainst(),
        getBanned(),
        new PriorityQueue<>(bestMatchups),
        new PriorityQueue<>(worstMatchups));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LineupMetadata metadata = (LineupMetadata) o;
    return Arrays.equals(playedAgainst, metadata.playedAgainst) &&
        Arrays.equals(banned, metadata.banned) &&
        Objects.equals(getBestMatchups(), metadata.getBestMatchups()) &&
        Objects.equals(getWorstMatchups(), metadata.getWorstMatchups());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getBestMatchups(), getWorstMatchups());
    result = 31 * result + Arrays.hashCode(playedAgainst);
    result = 31 * result + Arrays.hashCode(banned);
    return result;
  }

  @Override
  public String toString() {
    return "LineupMetadata{" +
        "playedAgainst=" + Arrays.toString(playedAgainst) +
        ", banned=" + Arrays.toString(banned) +
        ", bestMatchups=" + bestMatchups +
        ", worstMatchups=" + worstMatchups +
        '}';
  }

  /**
   * Converts this to a string displaying the best and worst matchups for this lineup.
   */
  public String toBestAndWorstMatchupsString() {
    StringBuilder sb = new StringBuilder();
    bestMatchups.stream().sorted(Pair.<Lineup>rightDoubleComparator().reversed())
        .forEach(p -> sb.append(p.first()).append(",").append(p.second()).append(","));
    worstMatchups.stream().sorted(Pair.rightDoubleComparator())
        .forEach(p -> sb.append(p.first()).append(",").append(p.second()).append(","));
    return sb.toString();
  }

  /**
   * Converts this to a string displaying the ban percentage of each deck played.
   */
  public String toBanPercentString(MatchupMatrix matchups) {
    Map<String, Double> banPercents = new HashMap<>();
    for (int i = 0; i < matchups.getHeaders().size(); i++) {
      int plays = playedAgainst[i];
      double bans = banned[i];

      if (plays > 0) {
        banPercents.put(matchups.getHeaderName(i), bans / (double) plays);
      }
    }

    return banPercents.entrySet()
        .stream()
        .sorted(comparingDouble((ToDoubleFunction<Map.Entry<?, Double>>) Map.Entry::getValue)
            .reversed())
        .map(e -> String.format("%s,%f,", e.getKey(), e.getValue()))
        .collect(Collectors.joining());
  }
}
