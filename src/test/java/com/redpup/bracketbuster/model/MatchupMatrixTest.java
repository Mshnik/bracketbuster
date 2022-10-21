package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MatchupMatrixTest {

  private static final MatchupMessage MATCHUP_MESSAGE_A_A
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("A")
      .setWins(2)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_B
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("B")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_A
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("A")
      .setWins(1)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_A_BAD_SYMMETRY
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("A")
      .setWins(2)
      .setGames(5)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_B
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("B")
      .setWins(1)
      .setGames(2)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_C
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("C")
      .setWins(1)
      .setGames(2)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_C
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("C")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_C
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("C")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_A_WITH_WIN_RATE =
      Matchups.populateWinRate(MATCHUP_MESSAGE_A_A);
  private static final MatchupMessage MATCHUP_MESSAGE_A_B_WITH_WIN_RATE =
      Matchups.populateWinRate(MATCHUP_MESSAGE_A_B);
  private static final MatchupMessage MATCHUP_MESSAGE_B_A_WITH_WIN_RATE =
      Matchups.populateWinRate(MATCHUP_MESSAGE_B_A);

  private static final MatchupMessage MATCHUP_MESSAGE_AB12_CD34
      = MatchupMessage.newBuilder()
      .setPlayer("A/B (1/2)")
      .setOpponent("C/D (3/4)")
      .setWins(2)
      .setGames(6)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_EF56_GH78
      = MatchupMessage.newBuilder()
      .setPlayer("E/F (5/6)")
      .setOpponent("G/H (7/8)")
      .setWins(2)
      .setGames(6)
      .build();


  @Test
  public void invalidConstruction_invalidSymmetry() {
    assertThrows(IllegalArgumentException.class,
        () -> MatchupMatrix.from(
            MATCHUP_MESSAGE_A_A,
            MATCHUP_MESSAGE_A_B,
            MATCHUP_MESSAGE_B_A_BAD_SYMMETRY));
  }

  @Test
  public void getNumDecks_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThat(matrix.getNumDecks()).isEqualTo(2);
  }

  @Test
  public void getTotalGames_hasSymmetry_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_B_B);

    assertThat(matrix.getTotalGames()).isEqualTo(12);
  }

  @Test
  public void getTotalGames_missingSymmetry_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_B);

    assertThat(matrix.getTotalGames()).isEqualTo(12);
  }

  @Test
  public void getPlayRate_hasSymmetry_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_B_B);

    assertThat(matrix.getPlayRate("A")).isEqualTo(7);
    assertThat(matrix.getPlayRate("B")).isEqualTo(5);
  }

  @Test
  public void getPlayRate_missingSymmetry_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_B);

    assertThat(matrix.getPlayRate("A")).isEqualTo(7);
    assertThat(matrix.getPlayRate("B")).isEqualTo(5);
  }

  @Test
  public void getHeaders_returnsValues() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThat(matrix.getHeaders()).containsExactly("A", "B").inOrder();
  }

  @Test
  public void hasMatchup_returnsCheck() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThat(matrix.hasMatchup("A", "B"))
        .isTrue();
    assertThat(matrix.hasMatchup("B", "A"))
        .isTrue();
    assertThat(matrix.hasMatchup("A", "A"))
        .isTrue();
    assertThat(matrix.hasMatchup("B", "B"))
        .isFalse();
  }

  @Test
  public void getMatchup_returnsValueOrNull() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThat(matrix.getMatchup("A", "B"))
        .isEqualTo(MATCHUP_MESSAGE_A_B_WITH_WIN_RATE);
    assertThat(matrix.getMatchup("B", "A"))
        .isEqualTo(MATCHUP_MESSAGE_B_A_WITH_WIN_RATE);
    assertThat(matrix.getMatchup("A", "A"))
        .isEqualTo(MATCHUP_MESSAGE_A_A_WITH_WIN_RATE);
    assertThat(matrix.getMatchup("B", "B"))
        .isNull();
  }

  @Test
  public void getMatchup_inverseIsPopulated() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B);

    assertThat(matrix.getMatchup("A", "B"))
        .isEqualTo(MATCHUP_MESSAGE_A_B_WITH_WIN_RATE);
    assertThat(matrix.getMatchup("B", "A"))
        .isEqualTo(MATCHUP_MESSAGE_B_A_WITH_WIN_RATE);
    assertThat(matrix.getMatchup("A", "A"))
        .isEqualTo(MATCHUP_MESSAGE_A_A_WITH_WIN_RATE);
  }

  @Test
  public void getMatchup_oobThrows() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup(-1, 1));
    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup(1, -1));
    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup(1, 1000000));
    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup(10000000, 1));
  }

  @Test
  public void getMatchup_unknownNameThrows() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup("A", "C"));
    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup("C", "A"));
  }

  @Test
  public void getHeaderIndex() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThat(matrix.getHeaderIndex("A")).isEqualTo(0);
    assertThat(matrix.getHeaderIndex("B")).isEqualTo(1);
  }

  @Test
  public void getHeaderIndex_unknownThrows() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getHeaderIndex("C"));
  }

  @Test
  public void getHeaderName() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThat(matrix.getHeaderName(0)).isEqualTo("A");
    assertThat(matrix.getHeaderName(1)).isEqualTo("B");
  }

  @Test
  public void getHeaderName_unknownThrows() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getHeaderName(-1));
  }

  @Test
  public void getHeaderWeight_byIndex() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_B_B);

    assertThat(matrix.getHeaderWeight(0)).isEqualTo(7.0 / 12.0);
    assertThat(matrix.getHeaderWeight(1)).isEqualTo(5.0 / 12.0);
  }

  @Test
  public void getHeaderWeight_byIndex_unknownThrows() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_B_B);

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getHeaderWeight(-1));
  }


  @Test
  public void getHeaderWeight_byName() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_B_B);

    assertThat(matrix.getHeaderWeight("A")).isEqualTo(7.0 / 12.0);
    assertThat(matrix.getHeaderWeight("B")).isEqualTo(5.0 / 12.0);
  }

  @Test
  public void getHeaderWeight_byName_unknownThrows() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_B_B);

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getHeaderWeight("C"));
  }


  @Test
  public void createAllValidLineups_notEnoughDecks() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_AB12_CD34);

    assertThat(matrix.createAllValidLineups()).isEmpty();
  }

  @Test
  public void createAllValidLineups_withLineups() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_AB12_CD34,
        MATCHUP_MESSAGE_EF56_GH78);

    assertThat(matrix.createAllValidLineups())
        .containsExactly(
            Lineup.ofDeckIndices(matrix, 0, 1, 2),
            Lineup.ofDeckIndices(matrix, 0, 1, 3),
            Lineup.ofDeckIndices(matrix, 0, 2, 3),
            Lineup.ofDeckIndices(matrix, 1, 2, 3));
  }

  @Test
  public void canPlay_returnsTrue() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_B_B,
        MATCHUP_MESSAGE_C_C,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_A_C,
        MATCHUP_MESSAGE_B_C);

    assertThat(matrix.canPlay(
        Lineup.ofDeckNames(matrix, "A", "B", "C"),
        Lineup.ofDeckNames(matrix, "A", "B", "C")))
        .isTrue();
  }

  @Test
  public void canPlay_returnsFalse() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A,
        MATCHUP_MESSAGE_A_C);

    assertThat(matrix.canPlay(
        Lineup.ofDeckNames(matrix, "A", "B", "C"),
        Lineup.ofDeckNames(matrix, "A", "B", "C")))
        .isFalse();
  }

}