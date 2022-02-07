package com.redpup.bracketbuster.model;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A combination of a specific set of decks to play.
 */
public final class Lineup {

  /**
   * Returns a new Lineup of the given decks by index.
   */
  public static Lineup ofDeckIndices(MatchupMatrix matchups, int... decks) {
    return new Lineup(
        Arrays.stream(decks).boxed().collect(toImmutableList()),
        Arrays.stream(decks).mapToObj(matchups::getHeaderName).collect(toImmutableList()),
        new LineupMetadata());
  }

  /**
   * Returns a new Lineup of the given decks by index.
   */
  public static Lineup ofDeckNames(MatchupMatrix matchups, String... decks) {
    return new Lineup(
        Arrays.stream(decks).map(matchups::getHeaderIndex).collect(toImmutableList()),
        ImmutableList.copyOf(decks),
        new LineupMetadata());
  }

  private final ImmutableList<Integer> decks;
  private final ImmutableList<String> deckNames;
  private final LineupMetadata metadata;

  private Lineup(List<Integer> decks, List<String> deckNames,
      LineupMetadata metadata) {
    this.decks = ImmutableList.copyOf(decks);
    this.deckNames = ImmutableList.copyOf(deckNames);
    this.metadata = metadata;
  }

  /**
   * Returns {@link #decks}.
   */
  public ImmutableList<Integer> getDecks() {
    return decks;
  }

  /**
   * Returns the deck at the requested index.
   */
  public int getDeck(int index) {
    return decks.get(index);
  }

  /**
   * Returns {@link #deckNames}.
   */
  public ImmutableList<String> getDeckNames() {
    return deckNames;
  }

  /**
   * Returns the name of the deck at the requested index.
   */
  public String getDeckName(int index) {
    return deckNames.get(index);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Lineup lineup = (Lineup) o;
    return Objects.equals(decks, lineup.decks) &&
        Objects.equals(deckNames, lineup.deckNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(decks, deckNames);
  }

  @Override
  public String toString() {
    return "Lineup{" +
        "decks=" + decks +
        ", deckNames=" + deckNames +
        ", metadata=" + metadata +
        '}';
  }
}
