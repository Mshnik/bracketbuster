package com.redpup.bracketbuster.model;


import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.Streams;
import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.util.Pair;
import com.redpup.bracketbuster.util.Strings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for operating on {@link MatchupMessage} and its wrappers.
 */
public final class Matchups {

  /**
   * Maximum number of digits to keep when calculating win rate.
   */
  private static final int WIN_RATE_SIG_FIGS = 9;
  private static final long WIN_RATE_ROUND_CONTEXT = (long) Math.pow(10, WIN_RATE_SIG_FIGS);

  private Matchups() {
  }

  /**
   * Populates {@link MatchupMessage#getWinRate()} in the given message. If it is already set,
   * asserts that it is set correctly.
   */
  static MatchupMessage populateWinRate(MatchupMessage message) {
    double winRate =
        (double) (message.getWins() * WIN_RATE_ROUND_CONTEXT / message.getGames())
            / WIN_RATE_ROUND_CONTEXT;
    checkState(winRate >= 0 && winRate <= 1, "Found invalid winRate: %s.\nMessage: %s", winRate,
        message);
    if (message.getWinRate() == 0.0 && message.getWins() > 0) {
      return message.toBuilder().setWinRate(winRate).build();
    } else {
      checkState(message.getWinRate() == winRate,
          "Currently populated win rate is incorrect. Expected %s, found %s", winRate,
          message.getWinRate());
      return message;
    }
  }

  /**
   * Returns the inverse of the given {@code matchup} by switching player and opponent.
   */
  public static MatchupMessage inverse(MatchupMessage matchup) {
    MatchupMessage.Builder builder = MatchupMessage.newBuilder()
        .setPlayer(matchup.getOpponent())
        .setOpponent(matchup.getPlayer())
        .setGames(matchup.getGames())
        .setWins(matchup.getGames() - matchup.getWins());

    if (matchup.getWinRate() > 0 || matchup.getWins() == 0) {
      return populateWinRate(builder.build());
    }

    return builder.build();
  }

  /**
   * Reads all matchups from {@code path} into a {@link MatchupList}.
   */
  public static MatchupList readMatchupListFromCsv(Path path) throws IOException {
    List<List<String>> csv =
        Files.readAllLines(path).stream()
            .map(s -> s.split(","))
            .map(Arrays::asList)
            .collect(toImmutableList());

    List<String> players = csv.stream()
        .map(lst -> lst.get(0))
        .skip(2)
        .map(Strings::sanitize)
        .collect(toImmutableList());

    Map<String, Double> opponentsAndPlayRates =
        Streams.zip(
            csv.get(0).stream().skip(1).map(Strings::sanitize),
            csv.get(1).stream().skip(1).map(Double::parseDouble),
            Pair::of).collect(Pair.toImmutableMap());

    MatchupList.Builder builder = MatchupList.newBuilder()
        .addAllPlayers(players)
        .putAllOpponent(opponentsAndPlayRates);

    for (int r = 2; r < csv.size(); r++) {
      for (int c = 1; c < csv.get(r).size(); c++) {
        builder.addMatchupsBuilder()
            .setPlayer(Strings.sanitize(csv.get(r).get(0)))
            .setOpponent(Strings.sanitize(csv.get(0).get(c)))
            .setWins((int) Double.parseDouble(csv.get(r).get(c)))
            .setGames(100);
      }
    }

    return builder.build();
  }
}
