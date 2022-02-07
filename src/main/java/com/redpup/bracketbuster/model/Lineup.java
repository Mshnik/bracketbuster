package com.redpup.bracketbuster.model;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

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
        new Metadata());
  }

  /**
   * Returns a new Lineup of the given decks by index.
   */
  public static Lineup ofDeckNames(MatchupMatrix matchups, String... decks) {
    return new Lineup(
        Arrays.stream(decks).map(matchups::getHeaderIndex).collect(toImmutableList()),
        ImmutableList.copyOf(decks),
        new Metadata());
  }

  private final ImmutableList<Integer> decks;
  private final ImmutableList<String> deckNames;
  private final Metadata metadata;

  private Lineup(List<Integer> decks, List<String> deckNames,
      Metadata metadata) {
    this.decks = ImmutableList.copyOf(decks);
    this.deckNames = ImmutableList.copyOf(deckNames);
    this.metadata = metadata;
  }



  /**
   * Mutable collector of matchup metadata for this lineup.
   */
  public static final class Metadata {

  }

}
