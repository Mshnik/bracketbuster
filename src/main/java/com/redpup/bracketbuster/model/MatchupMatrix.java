package com.redpup.bracketbuster.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableBiMap.toImmutableBiMap;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A 2D interpretation of a list of {@link MatchupMessage}s optimized for querying.
 */
public final class MatchupMatrix {

  /**
   * Returns a new {@link MatchupMatrix} from the given {@code list}.
   */
  public static MatchupMatrix fromProto(MatchupList list) {
    return new MatchupMatrix(list.getMatchupsList());
  }

  /**
   * Returns a new {@link MatchupMatrix} from the given {@code matchups}.
   */
  public static MatchupMatrix from(MatchupMessage... matchups) {
    return new MatchupMatrix(Arrays.asList(matchups));
  }

  private final ImmutableBiMap<String, Integer> headers;
  private final MatchupMessage[][] matchups;

  private MatchupMatrix(List<MatchupMessage> matchupsList) {
    headers =
        Streams.mapWithIndex(
            Stream.concat(
                matchupsList.stream().map(MatchupMessage::getPlayer),
                matchupsList.stream().map(MatchupMessage::getOpponent))
                .distinct()
                .sorted(),
            Pair::of
        ).collect(toImmutableBiMap(Pair::first, p -> p.second().intValue()));

    matchups = new MatchupMessage[headers.size()][headers.size()];
    for (MatchupMessage message : matchupsList) {
      Integer row = headers.get(message.getPlayer());
      Integer col = headers.get(message.getOpponent());

      checkArgument(row != null, "Player %s not found in %s", message.getPlayer(), headers);
      checkArgument(col != null, "Opponent %s not found in %s", message.getOpponent(), headers);

      MatchupMessage messageWithWinRate = Matchups.populateWinRate(message);

      checkArgument(matchups[row][col] == null || matchups[row][col].equals(messageWithWinRate),
          "Duplicate and disagreeing matchup %s vs %s.\nExisting: %s\nNew: %s", message.getPlayer(),
          message.getOpponent(), matchups[row][col], messageWithWinRate);

      matchups[row][col] = messageWithWinRate;
      if (!row.equals(col)) {
        matchups[col][row] = Matchups.inverse(messageWithWinRate);
      }
    }
  }

  /**
   * Returns the number of unique decks in this matchup matrix.
   */
  public int getNumDecks() {
    return headers.size();
  }

  /**
   * Returns {@link #headers} in order.
   */
  public ImmutableList<String> getHeaders() {
    return headers.keySet().asList();
  }

  /**
   * Returns the index of the given {@code headerName}.
   */
  public int getHeaderIndex(String headerName) {
    checkArgument(headers.containsKey(headerName), "Name %s not found: %s", headerName,
        headers);
    return headers.get(headerName);
  }

  /**
   * Returns the name of the header at the given {@code headerIndex}.
   */
  public String getHeaderName(int headerIndex) {
    checkArgument(headers.inverse().containsKey(headerIndex), "Index %s not found: %s", headerIndex,
        headers.inverse());
    return headers.inverse().get(headerIndex);
  }

  /**
   * Returns true iff the given matchup has data.
   */
  public boolean hasMatchup(int player, int opponent) {
    return matchups[player][opponent] != null;
  }

  /**
   * Returns true iff the given matchup has data.
   */
  public boolean hasMatchup(String player, String opponent) {
    Integer row = headers.get(player);
    Integer col = headers.get(opponent);

    checkArgument(row != null, "Player %s not found in %s", player, headers);
    checkArgument(col != null, "Opponent %s not found in %s", opponent, headers);

    return hasMatchup(row, col);
  }

  /**
   * Returns the matchup data between {@code player} and {@code opponent}. May return null if there
   * is no known matchup for these players.
   */
  public @Nullable MatchupMessage getMatchup(int player, int opponent) {
    checkArgument(player >= 0 && player < matchups.length, "Player OOB: %s", player);
    checkArgument(opponent >= 0 && opponent < matchups.length, "Opponent OOB: %s", player);

    return matchups[player][opponent];
  }

  /**
   * Returns the matchup data between {@code player} and {@code opponent}. May return null if there
   * is no known matchup for these players.
   */
  public @Nullable MatchupMessage getMatchup(String player, String opponent) {
    Integer row = headers.get(player);
    Integer col = headers.get(opponent);

    checkArgument(row != null, "Player %s not found in %s", player, headers);
    checkArgument(col != null, "Opponent %s not found in %s", opponent, headers);

    return getMatchup(row, col);
  }

  /**
   * Builds and returns a list of all valid {@link Lineup}s that can be build from this matchup
   * data. Assumes lineups have size {@link com.redpup.bracketbuster.util.Constants#PLAYER_DECK_COUNT}.
   */
  public ImmutableList<Lineup> createAllValidLineups() {
    return IntStream.range(0, headers.size()).boxed()
        .flatMap(deck1 -> IntStream.range(deck1 + 1, headers.size()).boxed()
            .flatMap(deck2 -> IntStream.range(deck2 + 1, headers.size()).boxed()
                .map(deck3 -> Lineup.ofDeckIndices(this, deck1, deck2, deck3))))
        .filter(Lineup::isValid)
        .collect(toImmutableList());
  }

  /**
   * Returns true iff {@code player} and {@code opponent} can play. Returns false if any pair of
   * decks between {@code player} and {@code opponent} have no matchup data.
   */
  public boolean canPlay(Lineup player, Lineup opponent) {
    return player.getDecks().stream()
        .flatMap(p -> opponent.getDecks().stream().map(o -> Pair.of(p, o)))
        .allMatch(pair -> hasMatchup(pair.first(), pair.second()));
  }
}
