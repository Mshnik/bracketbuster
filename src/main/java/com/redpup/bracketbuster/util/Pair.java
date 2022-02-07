package com.redpup.bracketbuster.util;

import com.google.auto.value.AutoValue;

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
}
