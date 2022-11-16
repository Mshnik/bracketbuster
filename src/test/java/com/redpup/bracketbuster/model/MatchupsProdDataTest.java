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

  private static final Path ALL_ODDS_CSV = Paths.get("src", "main", "resources", "stats_new.csv");

  private static MatchupList read() throws IOException {
    return readMatchupListFromCsv(ALL_ODDS_CSV);
  }

  @Test
  public void readsFile() throws IOException {
    assertThat(read()).isNotNull();
  }

  @Test
  public void readsCorrectNumberOfMatchups() throws IOException {
    assertThat(read().getMatchupsList()).hasSize(400);
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
    assertThat(matrix.createAllValidPlayerLineups()).hasSize(1346);
    assertThat(matrix.createAllValidOpponentLineups()).hasSize(1346);
  }
}
