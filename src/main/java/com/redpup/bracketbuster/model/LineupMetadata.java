package com.redpup.bracketbuster.model;

import java.util.Arrays;

/**
 * Mutable container for metadata of a given lineup.
 */
public final class LineupMetadata {

  private final int[] playedAgainst;
  private final int[] banned;

  LineupMetadata(int numDecks) {
    this.playedAgainst = new int[numDecks];
    this.banned = new int[numDecks];
  }

  /**
   * Returns {@link #playedAgainst}. The result is defensively copied; mutations will not be
   * reflected in this metadata.
   */
  public int[] getPlayedAgainst() {
    return Arrays.copyOf(playedAgainst, playedAgainst.length);
  }

  /**
   * Increments the count of playing against {@code deck}.
   */
  public void incrementPlayedAgainst(int deck) {
    playedAgainst[deck]++;
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
  public void incrementBanned(int deck) {
    banned[deck]++;
  }
}
