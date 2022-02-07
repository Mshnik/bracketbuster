package com.redpup.bracketbuster.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.util.Pair;
import java.util.Arrays;
import java.util.List;
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

  private final ImmutableMap<String, Integer> headers;
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
        ).collect(toImmutableMap(Pair::first, p -> p.second().intValue()));

    matchups = new MatchupMessage[headers.size()][headers.size()];
    for (MatchupMessage message : matchupsList) {
      Integer row = headers.get(message.getPlayer());
      Integer col = headers.get(message.getOpponent());

      checkArgument(row != null, "Player %s not found in %s", message.getPlayer(), headers);
      checkArgument(col != null, "Opponent %s not found in %s", message.getOpponent(), headers);

      checkArgument(matchups[row][col] == null, "Duplicate matchup %s vs %s", message.getPlayer(),
          message.getOpponent());
      matchups[row][col] = message;
    }
  }

  /**
   * Returns the matchup data between {@code player} and {@code opponent}. May return null if there
   * is no known matchup for these players.
   */
  public @Nullable MatchupMessage getMatchup(int player, int opponent) {
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
}
