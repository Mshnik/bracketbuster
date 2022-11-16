package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Correspondence;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MatchupMatrixTest {

  private static final double ERROR = 1.0e-8;

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
  private static final MatchupMessage MATCHUP_MESSAGE_C_A
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("A")
      .setWins(1)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_C
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("C")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_B
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("B")
      .setWins(1)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_A_WITH_WIN_RATE =
      Matchups.populateWinRate(MATCHUP_MESSAGE_A_A);
  private static final MatchupMessage MATCHUP_MESSAGE_A_B_WITH_WIN_RATE =
      Matchups.populateWinRate(MATCHUP_MESSAGE_A_B);
  private static final MatchupMessage MATCHUP_MESSAGE_B_A_WITH_WIN_RATE =
      Matchups.populateWinRate(MATCHUP_MESSAGE_B_A);

  private static final String A_B_1_2 = "A/B (1/2)";
  private static final String C_D_3_4 = "C/D (3/4)";
  private static final String E_F_5_6 = "E/F (5/6)";
  private static final String G_H_7_8 = "G/H (7/8)";

  private static final MatchupMessage MATCHUP_MESSAGE_AB12_AB12
      = MatchupMessage.newBuilder()
      .setPlayer(A_B_1_2)
      .setOpponent(A_B_1_2)
      .setWins(4)
      .setGames(8)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_AB12_CD34
      = MatchupMessage.newBuilder()
      .setPlayer(A_B_1_2)
      .setOpponent(C_D_3_4)
      .setWins(2)
      .setGames(6)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_EF56_GH78
      = MatchupMessage.newBuilder()
      .setPlayer(E_F_5_6)
      .setOpponent(G_H_7_8)
      .setWins(3)
      .setGames(6)
      .build();

  @Test
  public void getNumDecks_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.getNumDecks()).isEqualTo(2);
  }

  @Test
  public void getTotalGames_hasSymmetry_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A,
            MATCHUP_MESSAGE_B_B), ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.getNumDecks()).isEqualTo(2);
  }

  @Test
  public void getHeaders_returnsValues() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.getHeaders()).containsExactly("A", "B").inOrder();
  }

  @Test
  public void hasMatchup_returnsCheck() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

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
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

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
  public void getMatchup_oobThrows() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

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
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup("A", "C"));
    assertThrows(IllegalArgumentException.class,
        () -> matrix.getMatchup("C", "A"));
  }

  @Test
  public void getHeaderIndex() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.getHeaderIndex("A")).isEqualTo(0);
    assertThat(matrix.getHeaderIndex("B")).isEqualTo(1);
  }

  @Test
  public void getHeaderIndex_unknownThrows() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getHeaderIndex("C"));
  }

  @Test
  public void getHeaderName() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.getHeaderName(0)).isEqualTo("A");
    assertThat(matrix.getHeaderName(1)).isEqualTo("B");
  }

  @Test
  public void getHeaderName_unknownThrows() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThrows(IllegalArgumentException.class,
        () -> matrix.getHeaderName(-1));
  }

  @Test
  public void getHeaderWeight_returnsValue() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A,
            MATCHUP_MESSAGE_B_B), ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.getHeaderWeight("A")).isEqualTo(0.5);
  }

  @Test
  public void getHeaderWeight_returnsZeroForNonOpponent() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A,
            MATCHUP_MESSAGE_B_B), ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.getHeaderWeight("B")).isEqualTo(0.0);
  }

  @Test
  public void getHeaderWeight_unknownThrows() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A,
            MATCHUP_MESSAGE_B_B), ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThrows(IllegalArgumentException.class, () -> matrix.getHeaderWeight("C"));
  }

  @Test
  public void createAllValidPlayerLineups_notEnoughDecks() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_AB12_AB12, MATCHUP_MESSAGE_AB12_CD34,
            MATCHUP_MESSAGE_EF56_GH78),
            ImmutableList.of(A_B_1_2), ImmutableMap.of());

    assertThat(matrix.createAllValidPlayerLineups()).isEmpty();
  }

  @Test
  public void createAllValidPlayerLineups_withLineups() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_AB12_AB12, MATCHUP_MESSAGE_AB12_CD34,
            MATCHUP_MESSAGE_EF56_GH78),
            ImmutableList.of(A_B_1_2, C_D_3_4, E_F_5_6, G_H_7_8), ImmutableMap.of("A", 0.5));

    assertThat(matrix.createAllValidPlayerLineups())
        .containsExactly(
            Lineup.ofDeckIndices(matrix, 0, 1, 2),
            Lineup.ofDeckIndices(matrix, 0, 1, 3),
            Lineup.ofDeckIndices(matrix, 0, 2, 3),
            Lineup.ofDeckIndices(matrix, 1, 2, 3));
  }

  @Test
  public void createAllValidOpponentLineups_notEnoughDecks() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_AB12_AB12, MATCHUP_MESSAGE_AB12_CD34,
            MATCHUP_MESSAGE_EF56_GH78),
            ImmutableList.of(), ImmutableMap.of(A_B_1_2, 0.5));

    assertThat(matrix.createAllValidOpponentLineups()).isEmpty();
  }

  @Test
  public void createAllValidOpponentLineups_withLineups() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_AB12_AB12, MATCHUP_MESSAGE_AB12_CD34,
            MATCHUP_MESSAGE_EF56_GH78),
            ImmutableList.of(),
            ImmutableMap.of(A_B_1_2, 0.1, C_D_3_4, 0.2, E_F_5_6, 0.3, G_H_7_8, 0.5));

    assertThat(matrix.createAllValidOpponentLineups())
        .containsExactly(
            Lineup.ofDeckIndices(matrix, 0, 1, 2),
            Lineup.ofDeckIndices(matrix, 0, 1, 3),
            Lineup.ofDeckIndices(matrix, 0, 2, 3),
            Lineup.ofDeckIndices(matrix, 1, 2, 3));
  }

  @Test
  public void createWeightedValidOpponentLineups_withLineups_averageWeighting() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_AB12_AB12, MATCHUP_MESSAGE_AB12_CD34,
            MATCHUP_MESSAGE_EF56_GH78), ImmutableList.of(),
            ImmutableMap.of(A_B_1_2, 0.1, C_D_3_4, 0.2, E_F_5_6, 0.3, G_H_7_8, 0.5));

    assertThat(matrix.createWeightedValidOpponentLineups(LineupWeightType.AVERAGE))
        .comparingValuesUsing(Correspondence.tolerance(ERROR))
        .containsExactly(
            Lineup.ofDeckIndices(matrix, 0, 1, 2), 0.2,
            Lineup.ofDeckIndices(matrix, 0, 1, 3), 0.26666666666,
            Lineup.ofDeckIndices(matrix, 0, 2, 3), 0.3,
            Lineup.ofDeckIndices(matrix, 1, 2, 3), 0.3333333333);
  }

  @Test
  public void createWeightedValidLineups_withLineups_geometricWeighting() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_AB12_AB12, MATCHUP_MESSAGE_AB12_CD34,
            MATCHUP_MESSAGE_EF56_GH78), ImmutableList.of(),
            ImmutableMap.of(A_B_1_2, 0.1, C_D_3_4, 0.2, E_F_5_6, 0.3, G_H_7_8, 0.5));

    assertThat(matrix.createWeightedValidOpponentLineups(LineupWeightType.GEOMETRIC))
        .comparingValuesUsing(Correspondence.tolerance(ERROR))
        .containsExactly(
            Lineup.ofDeckIndices(matrix, 0, 1, 2), 0.1817120592,
            Lineup.ofDeckIndices(matrix, 0, 1, 3), 0.2154434690,
            Lineup.ofDeckIndices(matrix, 0, 2, 3), 0.2466212074,
            Lineup.ofDeckIndices(matrix, 1, 2, 3), 0.3107232505);
  }

  @Test
  public void canPlay_returnsTrue() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_B_B, MATCHUP_MESSAGE_C_C,
            MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A, MATCHUP_MESSAGE_A_C,
            MATCHUP_MESSAGE_C_A, MATCHUP_MESSAGE_B_C, MATCHUP_MESSAGE_C_B),
            ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.canPlay(
        Lineup.ofDeckNames(matrix, "A", "B", "C"),
        Lineup.ofDeckNames(matrix, "A", "B", "C")))
        .isTrue();
  }

  @Test
  public void canPlay_returnsFalse() {
    MatchupMatrix matrix = MatchupMatrix
        .from(ImmutableList.of(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_B_A,
            MATCHUP_MESSAGE_A_C), ImmutableList.of("A", "B"), ImmutableMap.of("A", 0.5));

    assertThat(matrix.canPlay(
        Lineup.ofDeckNames(matrix, "A", "B", "C"),
        Lineup.ofDeckNames(matrix, "A", "B", "C")))
        .isFalse();
  }

}