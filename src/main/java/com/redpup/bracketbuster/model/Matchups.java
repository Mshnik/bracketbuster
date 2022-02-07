package com.redpup.bracketbuster.model;


import static com.redpup.bracketbuster.util.Strings.sanitize;

import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.util.Strings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility methods for operating on {@link MatchupMessage} and its wrappers.
 */
public final class Matchups {

  private Matchups() {
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
