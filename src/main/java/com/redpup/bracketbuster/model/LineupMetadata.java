package com.redpup.bracketbuster.model;

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

  public void playAgainst(int deck) {}

  public void banned(int deck) {

  }
}
