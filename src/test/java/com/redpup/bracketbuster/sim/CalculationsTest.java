package com.redpup.bracketbuster.sim;


import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.model.Matchups.inverse;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.collect.Range;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CalculationsTest {

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
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.0}
    })).isEqualTo(0);
  }

  @Test
  public void banPlayerDeck_allDecksEqual_bansFirstIndex() {
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5}
    })).isEqualTo(0);
  }

  @Test
  public void banPlayerDeck_bansBestDeck() {
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.9, 0.9, 0.9},
        {0.1, 0.1, 0.1}
    })).isEqualTo(1);
  }

  @Test
  public void banPlayerDeck_bansBestDeckOverall() {
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.9, 0.5, 0.1},
        {0.5, 0.9, 0.5}
    })).isEqualTo(2);
  }


  @Test
  public void banOpponentDeck_oneDeck() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.0}
    })).isEqualTo(0);
  }

  @Test
  public void banOpponentDeck_allDecksEqual_bansFirstIndex() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5}
    })).isEqualTo(0);
  }

  @Test
  public void banOpponentDeck_bansWorstDeckForPlayer() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.5, 0.5, 0.1},
        {0.5, 0.9, 0.1},
        {0.2, 0.5, 0.1}
    })).isEqualTo(2);
  }

  @Test
  public void banOpponentDeck_bansWorstDeckForPlayerOverall() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.5, 0.5, 0.1},
        {0.9, 0.5, 0.2},
        {0.5, 0.3, 0.5}
    })).isEqualTo(2);
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
  public void winRateBestTwoOfThreeOneBan_oneOpponent() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_1,
        MATCHUP_MESSAGE_A_2,
        MATCHUP_MESSAGE_A_3,
        MATCHUP_MESSAGE_B_1,
        MATCHUP_MESSAGE_B_2,
        MATCHUP_MESSAGE_B_3,
        MATCHUP_MESSAGE_C_1,
        MATCHUP_MESSAGE_C_2,
        MATCHUP_MESSAGE_C_3);

    Lineup player = Lineup.ofDeckNames(matrix, "A", "B", "C");
    Lineup opponent = Lineup.ofDeckNames(matrix, "1", "2", "3");

    double winRate = Calculations.winRateBestTwoOfThreeOneBan(
        player, opponent, matrix);

    assertThat(winRate).isIn(Range.open(0.0, 1.0));

    assertThat(player.metadata().getPlayedAgainst()).asList()
        .containsExactly(1, 1, 1, 0, 0, 0).inOrder();
    assertThat(player.metadata().getBanned()).asList()
        .containsExactly(1, 0, 0, 0, 0, 0).inOrder();
  }

}