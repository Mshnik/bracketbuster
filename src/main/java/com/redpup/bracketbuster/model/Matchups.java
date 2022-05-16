package com.redpup.bracketbuster.model;


import static com.google.common.base.Preconditions.checkState;
import static com.redpup.bracketbuster.util.Strings.sanitize;

import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility methods for operating on {@link MatchupMessage} and its wrappers.
 */
public final class Matchups {

  /** Maximum number of digits to keep when calculating win rate. */
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
  public static MatchupList readMatchupListFromCsv(
      Path path,
      int playerIndex,
      int opponentIndex,
      int gamesIndex,
      int winsIndex) throws IOException {
    MatchupList.Builder builder = MatchupList.newBuilder();

    Files.readAllLines(path).stream()
        // Skip header line.
        .skip(1)
        .map(s -> s.split(","))
        .map(arr -> MatchupMessage.newBuilder()
            .setPlayer(sanitize(arr[playerIndex]))
            .setOpponent(sanitize(arr[opponentIndex]))
            .setGames(Integer.parseInt(arr[gamesIndex]))
            .setWins(Integer.parseInt(arr[winsIndex]))
            .build())
        .map(Matchups::populateWinRate)
        .forEach(builder::addMatchups);

    return builder.build();
  }
}
