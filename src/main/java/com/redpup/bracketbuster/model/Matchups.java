package com.redpup.bracketbuster.model;


import static com.redpup.bracketbuster.util.Strings.sanitize;

import com.redpup.bracketbuster.model.proto.Matchup;
import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility methods for operating on {@link MatchupMessage} and its wrappers.
 */
public final class Matchups {

  private Matchups() {
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
      builder.setWinRate(1 - matchup.getWinRate());
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
            .setWins(Integer.parseInt(arr[winsIndex])))
        .map(b -> b.setWinRate((double) b.getWins() / b.getGames()).build())
        .forEach(builder::addMatchups);

    return builder.build();
  }
}
