package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.redpup.bracketbuster.model.Matchups.inverse;
import static com.redpup.bracketbuster.model.Matchups.populateWinRate;
import static com.redpup.bracketbuster.model.Matchups.readMatchupListFromCsv;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

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
public final class MatchupsTest {

  private static final Path ALL_ODDS_CSV = Paths.get("src", "test", "resources", "stats_new.csv");

  private static MatchupList read() throws IOException {
    return readMatchupListFromCsv(ALL_ODDS_CSV);
  }

  @Test
  public void inverse_reversesValues_noWinRate() {
    assertThat(
        inverse(
            MatchupMessage.newBuilder()
                .setPlayer("A")
                .setOpponent("B")
                .setGames(10)
                .setWins(4)
                .build()))
        .isEqualTo(
            MatchupMessage.newBuilder()
                .setOpponent("A")
                .setPlayer("B")
                .setGames(10)
                .setWins(6)
                .build());
  }

  @Test
  public void inverse_reversesValues_withWinRate() {
    assertThat(
        inverse(
            MatchupMessage.newBuilder()
                .setPlayer("A")
                .setOpponent("B")
                .setGames(10)
                .setWins(4)
                .setWinRate(0.4)
                .build()))
        .isEqualTo(
            MatchupMessage.newBuilder()
                .setOpponent("A")
                .setPlayer("B")
                .setGames(10)
                .setWins(6)
                .setWinRate(0.6)
                .build());
  }

  @Test
  public void inverse_reversesValues_withWinRate_fromNoWins() {
    assertThat(
        inverse(
            MatchupMessage.newBuilder()
                .setPlayer("A")
                .setOpponent("B")
                .setGames(10)
                .setWins(0)
                .build()))
        .isEqualTo(
            MatchupMessage.newBuilder()
                .setOpponent("A")
                .setPlayer("B")
                .setGames(10)
                .setWins(10)
                .setWinRate(1.0)
                .build());
  }

  @Test
  public void populateWinRate_noWins() {
    assertThat(
        populateWinRate(
            MatchupMessage.newBuilder()
                .setPlayer("A")
                .setOpponent("B")
                .setGames(10)
                .setWins(0)
                .build()))
        .isEqualTo(
            populateWinRate(
                MatchupMessage.newBuilder()
                    .setPlayer("A")
                    .setOpponent("B")
                    .setGames(10)
                    .setWins(0)
                    .build()));
  }

  @Test
  public void populateWinRate_setsWinRate() {
    assertThat(
        populateWinRate(
            MatchupMessage.newBuilder()
                .setPlayer("A")
                .setOpponent("B")
                .setGames(10)
                .setWins(4)
                .build()))
        .isEqualTo(
            populateWinRate(
                MatchupMessage.newBuilder()
                    .setPlayer("A")
                    .setOpponent("B")
                    .setGames(10)
                    .setWins(4)
                    .setWinRate(0.4)
                    .build()));
  }

  @Test
  public void populateWinRate_checksWinRate() {
    assertThat(
        populateWinRate(
            MatchupMessage.newBuilder()
                .setPlayer("A")
                .setOpponent("B")
                .setGames(10)
                .setWins(4)
                .setWinRate(0.4)
                .build()))
        .isEqualTo(
            populateWinRate(
                MatchupMessage.newBuilder()
                    .setPlayer("A")
                    .setOpponent("B")
                    .setGames(10)
                    .setWins(4)
                    .setWinRate(0.4)
                    .build()));
  }


  @Test
  public void populateWinRate_checksWinRate_throwsIfWrong() {
    assertThrows(
        IllegalStateException.class, () ->
            populateWinRate(
                MatchupMessage.newBuilder()
                    .setPlayer("A")
                    .setOpponent("B")
                    .setGames(10)
                    .setWins(4)
                    .setWinRate(0.7)
                    .build()));
  }

  @Test
  public void readsFile() throws IOException {
    assertThat(read()).isNotNull();
  }

  @Test
  public void readsCorrectNumberOfMatchups() throws IOException {
    assertThat(read().getMatchupsList()).hasSize(484);
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
}
