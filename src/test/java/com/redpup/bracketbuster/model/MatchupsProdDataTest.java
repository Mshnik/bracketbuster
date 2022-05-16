package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.redpup.bracketbuster.model.Matchups.readMatchupListFromCsv;

import com.google.common.collect.Range;
import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.util.Strings;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class MatchupsProdDataTest {

  private static final Path ALL_ODDS_CSV = Paths.get("src", "main", "resources", "stats.csv");

  /**
   * Column index of the filter value in {@link #ALL_ODDS_CSV}.
   */
  private static final int FILTER_INDEX = 4;

  /**
   * Column index of the player's deck in {@link #ALL_ODDS_CSV}.
   */
  private static final int PLAYER_INDEX = 6;

  /**
   * Column index of the opponent's deck in {@link #ALL_ODDS_CSV}.
   */
  private static final int OPPONENT_INDEX = 7;

  /**
   * Column index of the number of wins in {@link #ALL_ODDS_CSV}.
   */
  private static final int WINS_INDEX = 8;

  /**
   * Column index of the number of games in {@link #ALL_ODDS_CSV}.
   */
  private static final int GAMES_INDEX = 9;

  private static MatchupList read() throws IOException {
    return readMatchupListFromCsv(ALL_ODDS_CSV,
        FILTER_INDEX, PLAYER_INDEX, OPPONENT_INDEX, GAMES_INDEX, WINS_INDEX);
  }

  @Test
  public void readsFile() throws IOException {
    assertThat(read()).isNotNull();
  }

  @Test
  public void readsCorrectNumberOfMatchups() throws IOException {
    assertThat(read().getMatchupsList()).hasSize(1569);
  }

  @Test
  public void playerAndOpponentAreSanitized() throws IOException {
    MatchupList list = read();
    for (MatchupMessage matchup : list.getMatchupsList()) {
      assertWithMessage(matchup.getPlayer())
          .that(matchup.getPlayer())
          .isEqualTo(Strings.sanitize(matchup.getPlayer()));
      assertWithMessage(matchup.getOpponent())
          .that(matchup.getOpponent())
          .isEqualTo(Strings.sanitize(matchup.getOpponent()));
    }
  }

  @Test
  public void winRateIsPopulated() throws IOException {
    MatchupList list = read();
    for (MatchupMessage matchup : list.getMatchupsList()) {
      assertWithMessage(matchup.toString())
          .that(matchup.getWinRate())
          .isIn(Range.closed(0.0, 1.0));
    }
  }

  @Test
  public void createsValidLineups() throws IOException {
    MatchupList list = read();
    MatchupMatrix matrix = MatchupMatrix.fromProto(list);
    assertThat(matrix.createAllValidLineups()).hasSize(9600);
  }
}
