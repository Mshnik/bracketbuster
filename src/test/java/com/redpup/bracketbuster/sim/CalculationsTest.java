package com.redpup.bracketbuster.sim;


import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CalculationsTest {

  private static final double ERROR = 1.0e-8;

  private static final MatchupMessage MATCHUP_MESSAGE_A_1
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("1")
      .setWins(1)
      .setGames(2)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_2
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("2")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_3
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("3")
      .setWins(1)
      .setGames(3)
      .build();

  private static final MatchupMessage MATCHUP_MESSAGE_B_1
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("1")
      .setWins(6)
      .setGames(10)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_2
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("2")
      .setWins(7)
      .setGames(13)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_3
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("3")
      .setWins(5)
      .setGames(6)
      .build();

  private static final MatchupMessage MATCHUP_MESSAGE_C_1
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("1")
      .setWins(2)
      .setGames(10)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_2
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("2")
      .setWins(12)
      .setGames(13)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_3
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("3")
      .setWins(3)
      .setGames(6)
      .build();

  private static final MatchupMessage MATCHUP_MESSAGE_A_1_LOSE
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("1")
      .setWins(0)
      .setGames(2)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_1_LOSE
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("1")
      .setWins(0)
      .setGames(10)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_1_LOSE
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("1")
      .setWins(0)
      .setGames(10)
      .build();

  @Test
  public void maxIndex() {
    assertThat(Calculations.maxIndex()).isEqualTo(-1);
    assertThat(Calculations.maxIndex(0.0)).isEqualTo(0);
    assertThat(Calculations.maxIndex(0.0, 1.0)).isEqualTo(1);
    assertThat(Calculations.maxIndex(1.0, 0.0)).isEqualTo(0);
    assertThat(Calculations.maxIndex(0.0, -1.0, 1.0)).isEqualTo(2);
  }

  @Test
  public void minIndex() {
    assertThat(Calculations.minIndex()).isEqualTo(-1);
    assertThat(Calculations.minIndex(0.0)).isEqualTo(0);
    assertThat(Calculations.minIndex(0.0, 1.0)).isEqualTo(0);
    assertThat(Calculations.minIndex(1.0, 0.0)).isEqualTo(1);
    assertThat(Calculations.minIndex(0.0, -1.0, 1.0)).isEqualTo(1);
  }

  @Test
  public void winBestTwoOfThree_throwsOnBadArrayLength() {
    assertThrows(IllegalArgumentException.class, Calculations::winRateBestTwoOfThree);
    assertThrows(IllegalArgumentException.class, () -> Calculations.winRateBestTwoOfThree(0.0));
    assertThrows(IllegalArgumentException.class,
        () -> Calculations.winRateBestTwoOfThree(0.0, 0.0, 0.0, 0.0, 0.0));
  }

  @Test
  public void winBestTwoOfThree_throwsOnValueOOB() {
    assertThrows(IllegalArgumentException.class,
        () -> Calculations.winRateBestTwoOfThree(0.0, 1.0, 1.0, 1.1));
    assertThrows(IllegalArgumentException.class,
        () -> Calculations.winRateBestTwoOfThree(0.0, -0.1, 1.0, 1.1));
  }

  @Test
  public void winBestTwoOfThree_allZeroes() {
    assertThat(Calculations.winRateBestTwoOfThree(0.0, 0.0, 0.0, 0.0))
        .isEqualTo(0.0);
  }

  @Test
  public void winBestTwoOfThree_lowOdds() {
    assertThat(Calculations.winRateBestTwoOfThree(0.25, 0.25, 0.25, 0.25))
        .isIn(Range.openClosed(0.0, 0.5));
  }

  @Test
  public void winBestTwoOfThree_allPointFive() {
    assertThat(Calculations.winRateBestTwoOfThree(0.5, 0.5, 0.5, 0.5))
        .isEqualTo(0.5);
  }

  @Test
  public void winBestTwoOfThree_highOdds() {
    assertThat(Calculations.winRateBestTwoOfThree(0.75, 0.75, 0.75, 0.75))
        .isIn(Range.closedOpen(0.5, 1.0));
  }

  @Test
  public void winBestTwoOfThree_allOnes() {
    assertThat(Calculations.winRateBestTwoOfThree(1.0, 1.0, 1.0, 1.0))
        .isEqualTo(1.0);
  }

  @Test
  public void winBestTwoOfThree_swapPlayerDecks() {
    double p1o1 = 0.1;
    double p1o2 = 0.25;
    double p2o1 = 0.55;
    double p2o2 = 0.89;

    assertThat(Calculations.winRateBestTwoOfThree(p1o1, p1o2, p2o1, p2o2))
        .isEqualTo(Calculations.winRateBestTwoOfThree(p2o1, p2o2, p1o1, p1o2));
  }

  @Test
  public void winBestTwoOfThree_swapOpponentDecks() {
    double p1o1 = 0.1;
    double p1o2 = 0.25;
    double p2o1 = 0.55;
    double p2o2 = 0.89;

    assertThat(Calculations.winRateBestTwoOfThree(p1o1, p1o2, p2o1, p2o2))
        .isEqualTo(Calculations.winRateBestTwoOfThree(p1o2, p1o1, p2o2, p2o1));
  }

  @Test
  public void banPlayerDeck_oneDeck() {
    assertThat(Calculations.banPlayerDeckNaive(new double[][]{
        {0.0}
    })).isEqualTo(0);
  }

  @Test
  public void banPlayerDeck_allDecksEqual_bansFirstIndex() {
    assertThat(Calculations.banPlayerDeckNaive(new double[][]{
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5}
    })).isEqualTo(0);
  }

  @Test
  public void banPlayerDeck_bansBestDeck() {
    assertThat(Calculations.banPlayerDeckNaive(new double[][]{
        {0.5, 0.5, 0.5},
        {0.9, 0.9, 0.9},
        {0.1, 0.1, 0.1}
    })).isEqualTo(1);
  }

  @Test
  public void banPlayerDeck_bansBestDeckOverall() {
    assertThat(Calculations.banPlayerDeckNaive(new double[][]{
        {0.5, 0.5, 0.5},
        {0.9, 0.5, 0.1},
        {0.5, 0.9, 0.5}
    })).isEqualTo(2);
  }


  @Test
  public void banOpponentDeckNaive_oneDeck() {
    assertThat(Calculations.banOpponentDeckNaive(new double[][]{
        {0.0}
    })).isEqualTo(0);
  }

  @Test
  public void banOpponentDeckNaive_allDecksEqual_bansFirstIndex() {
    assertThat(Calculations.banOpponentDeckNaive(new double[][]{
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5}
    })).isEqualTo(0);
  }

  @Test
  public void banOpponentDeckNaive_bansWorstDeckForPlayer() {
    assertThat(Calculations.banOpponentDeckNaive(new double[][]{
        {0.5, 0.5, 0.1},
        {0.5, 0.9, 0.1},
        {0.2, 0.5, 0.1}
    })).isEqualTo(2);
  }

  @Test
  public void banOpponentDeckNaive_bansWorstDeckForPlayerOverall() {
    assertThat(Calculations.banOpponentDeckNaive(new double[][]{
        {0.5, 0.5, 0.1},
        {0.9, 0.5, 0.2},
        {0.5, 0.3, 0.5}
    })).isEqualTo(2);
  }

  @Test
  public void computeExpectedWinRatesWithBans() {
    double a1 = 0.5;
    double a2 = 0.5;
    double a3 = 0.1;
    double b1 = 0.9;
    double b2 = 0.5;
    double b3 = 0.2;
    double c1 = 0.5;
    double c2 = 0.3;
    double c3 = 0.5;

    assertThat(Calculations.computeExpectedWinRatesWithBans(new double[][]{
        {a1, a2, a3},
        {b1, b2, b3},
        {c1, c2, c3}
    })).isEqualTo(
        new double[][]{
            {
                Calculations.winRateBestTwoOfThree(b2, b3, c2, c3),
                Calculations.winRateBestTwoOfThree(b1, b3, c1, c3),
                Calculations.winRateBestTwoOfThree(b1, b2, c1, c2),
            },
            {
                Calculations.winRateBestTwoOfThree(a2, a3, c2, c3),
                Calculations.winRateBestTwoOfThree(a1, a3, c1, c3),
                Calculations.winRateBestTwoOfThree(a1, a2, c1, c2),
            },
            {
                Calculations.winRateBestTwoOfThree(a2, a3, b2, b3),
                Calculations.winRateBestTwoOfThree(a1, a3, b1, b3),
                Calculations.winRateBestTwoOfThree(a1, a2, b1, b2),
            }
        }

    );

  }

  @Test
  public void dropBannedDecksAndFlatten() {
    assertThat(Calculations.dropBannedDecksAndFlatten(
        new double[][]{
            {0.1, 0.2, 0.3},
            {0.4, 0.5, 0.6},
            {0.7, 0.8, 0.9}},
        1, 2
    )).isEqualTo(new double[]{0.1, 0.2, 0.7, 0.8});
  }

  @Test
  public void winRateBestTwoOfThreeOneBan_naive() {
    MatchupMatrix matrix = MatchupMatrix.from(
        ImmutableList.of(MATCHUP_MESSAGE_A_1,
        MATCHUP_MESSAGE_A_2,
        MATCHUP_MESSAGE_A_3,
        MATCHUP_MESSAGE_B_1,
        MATCHUP_MESSAGE_B_2,
        MATCHUP_MESSAGE_B_3,
        MATCHUP_MESSAGE_C_1,
        MATCHUP_MESSAGE_C_2,
        MATCHUP_MESSAGE_C_3),
        ImmutableList.of(), ImmutableMap.of()
        );

    Lineup player = Lineup.ofDeckNames(matrix, "A", "B", "C");
    Lineup opponent = Lineup.ofDeckNames(matrix, "1", "2", "3");

    double winRate = Calculations.winRateBestTwoOfThreeOneBanNaive(
        player, opponent, matrix);

    assertThat(winRate).isIn(Range.open(0.0, 1.0));

    assertThat(player.metadata().getPlayedAgainst()).asList()
        .containsExactly(1, 1, 1, 0, 0, 0).inOrder();
    assertThat(player.metadata().getBanned()).usingTolerance(ERROR)
        .containsExactly(1.0, 0.0, 0.0, 0.0, 0.0, 0.0).inOrder();
  }

  @Test
  public void winRateBestTwoOfThreeOneBan_nash() {
    MatchupMatrix matrix = MatchupMatrix.from(
        ImmutableList.of(MATCHUP_MESSAGE_A_1,
        MATCHUP_MESSAGE_A_2,
        MATCHUP_MESSAGE_A_3,
        MATCHUP_MESSAGE_B_1,
        MATCHUP_MESSAGE_B_2,
        MATCHUP_MESSAGE_B_3,
        MATCHUP_MESSAGE_C_1,
        MATCHUP_MESSAGE_C_2,
        MATCHUP_MESSAGE_C_3)
        ,
        ImmutableList.of(), ImmutableMap.of()
    );

    Lineup player = Lineup.ofDeckNames(matrix, "A", "B", "C");
    Lineup opponent = Lineup.ofDeckNames(matrix, "1", "2", "3");

    double winRate = Calculations.winRateBestTwoOfThreeOneBanNash(
        player, opponent, matrix);

    assertThat(winRate).isIn(Range.open(0.0, 1.0));

    assertThat(player.metadata().getPlayedAgainst()).asList()
        .containsExactly(1, 1, 1, 0, 0, 0).inOrder();
    assertThat(player.metadata().getBanned()).usingTolerance(ERROR)
        .containsExactly(0.0, 0.0, 1.0, 0.0, 0.0, 0.0)
        .inOrder();
  }

  @Test
  public void winRateBestTwoOfThreeOneBan_nash_alwaysBanOneDeck() {
    MatchupMatrix matrix = MatchupMatrix.from(
        ImmutableList.of(MATCHUP_MESSAGE_A_1_LOSE,
        MATCHUP_MESSAGE_A_2,
        MATCHUP_MESSAGE_A_3,
        MATCHUP_MESSAGE_B_1_LOSE,
        MATCHUP_MESSAGE_B_2,
        MATCHUP_MESSAGE_B_3,
        MATCHUP_MESSAGE_C_1_LOSE,
        MATCHUP_MESSAGE_C_2,
        MATCHUP_MESSAGE_C_3),
        ImmutableList.of(), ImmutableMap.of());

    Lineup player = Lineup.ofDeckNames(matrix, "A", "B", "C");
    Lineup opponent = Lineup.ofDeckNames(matrix, "1", "2", "3");

    double winRate = Calculations.winRateBestTwoOfThreeOneBanNash(
        player, opponent, matrix);

    assertThat(winRate).isIn(Range.open(0.0, 1.0));

    assertThat(player.metadata().getPlayedAgainst()).asList()
        .containsExactly(1, 1, 1, 0, 0, 0).inOrder();
    assertThat(player.metadata().getBanned()).usingTolerance(ERROR)
        .containsExactly(1.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        .inOrder();
  }

}