package com.redpup.bracketbuster.model;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;

/**
 * Mutable container for metadata of a given lineup.
 */
public final class LineupMetadata {

  private final int[] playedAgainst;
  private final int[] banned;

  LineupMetadata(int numDecks) {
    this(new int[numDecks], new int[numDecks]);
  }

  private LineupMetadata(int[] playedAgainst, int[] banned) {
    this.playedAgainst = playedAgainst;
    this.banned = banned;
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
  public int[] getBanned() {
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
   * Copies this metadata. This and the copy will have equivalent but separate state, so mutations
   * to this will not affect copy and vice-versa.
   */
  public LineupMetadata copy() {
    return new LineupMetadata(getPlayedAgainst(), getBanned());
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
        Arrays.equals(banned, metadata.banned);
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(playedAgainst);
    result = 31 * result + Arrays.hashCode(banned);
    return result;
  }

  @Override
  public String toString() {
    return "LineupMetadata{" +
        "playedAgainst=" + Arrays.toString(playedAgainst) +
        ", banned=" + Arrays.toString(banned) +
        '}';
  }
}
