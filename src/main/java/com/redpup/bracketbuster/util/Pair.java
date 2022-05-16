package com.redpup.bracketbuster.util;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import java.util.Comparator;
import java.util.stream.Collector;

/**
 * Simple pair class, with equality support.
 */
@AutoValue
public abstract class Pair<A, B> {

  Pair() {
  }

  /**
   * Returns a pair of the given args.
   */
  public static <A, B> Pair<A, B> of(A first, B second) {
    return new com.redpup.bracketbuster.util.AutoValue_Pair<>(first, second);
  }

  /**
   * The first/left component of the pair.
   */
  public abstract A first();

  /**
   * The second/right component of the pair.
   */
  public abstract B second();

  /**
   * Returns a collector that collects a stream of {@code Pair<A, B>} to a {@link ImmutableMap} of
   * {@code <A, B>}.
   */
  public static <A, B> Collector<Pair<A, B>, ?, ImmutableMap<A, B>> toImmutableMap() {
    return ImmutableMap.toImmutableMap(Pair::first, Pair::second);
  }

  /**
   * Returns a comparator for comparing pairs where the second half is a double.
   */
  public static <A> Comparator<Pair<A, Double>> rightDoubleComparator() {
    return Comparator.comparingDouble(Pair::second);
  }
}
