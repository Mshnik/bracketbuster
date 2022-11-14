package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LineupTest {

  private static final MatchupMessage MATCHUP_MESSAGE_A_B
      = MatchupMessage.newBuilder()
      .setPlayer("A (IO/NX)")
      .setOpponent("B (IO/NX)")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_A
      = MatchupMessage.newBuilder()
      .setPlayer("B (IO/NX)")
      .setOpponent("A (IO/NX)")
      .setWins(1)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_A
      = MatchupMessage.newBuilder()
      .setPlayer("A (IO/NX)")
      .setOpponent("A (IO/NX)")
      .setWins(2)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_C
      = MatchupMessage.newBuilder()
      .setPlayer("A (IO/NX)")
      .setOpponent("C (DE/FJ)")
      .setWins(1)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_A
      = MatchupMessage.newBuilder()
      .setPlayer("C (DE/FJ)")
      .setOpponent("A (IO/NX)")
      .setWins(3)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_D
      = MatchupMessage.newBuilder()
      .setPlayer("A (IO/NX)")
      .setOpponent("D (IO/SH)")
      .setWins(1)
      .setGames(5)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_D_A
      = MatchupMessage.newBuilder()
      .setPlayer("D (IO/SH)")
      .setOpponent("A (IO/NX)")
      .setWins(4)
      .setGames(5)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_ZAA_B
      = MatchupMessage.newBuilder()
      .setPlayer("Z/A/A (IO/SH)")
      .setOpponent("B (IO/NX)")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_ZAB_B
      = MatchupMessage.newBuilder()
      .setPlayer("Z/A/B (IO/FJ)")
      .setOpponent("B (IO/NX)")
      .setWins(2)
      .setGames(3)
      .build();

  private static final MatchupMatrix MATCHUP_MATRIX
      = MatchupMatrix.from(
      ImmutableList.of(MATCHUP_MESSAGE_A_A,
          MATCHUP_MESSAGE_A_B,
          MATCHUP_MESSAGE_A_C,
          MATCHUP_MESSAGE_A_D,
          MATCHUP_MESSAGE_B_A,
          MATCHUP_MESSAGE_C_A,
          MATCHUP_MESSAGE_D_A,
          MATCHUP_MESSAGE_ZAA_B,
          MATCHUP_MESSAGE_ZAB_B),
      ImmutableList.of("A (IO/NX)"),
      ImmutableMap.of("Z/A/B (IO/FJ)", 0.5));


  @Test
  public void buildLineup_fromIndices() {
    Lineup lineup = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 2, 3);
    assertThat(lineup.getDecks()).containsExactly(0, 2, 3).inOrder();
    assertThat(lineup.getDeckNames()).containsExactly("A (IO/NX)", "C (DE/FJ)", "D (IO/SH)")
        .inOrder();
  }

  @Test
  public void buildLineup_fromNames() {
    Lineup lineup = Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "C (DE/FJ)", "D (IO/SH)");
    assertThat(lineup.getDeckNames()).containsExactly("A (IO/NX)", "C (DE/FJ)", "D (IO/SH)")
        .inOrder();
    assertThat(lineup.getDecks()).containsExactly(0, 2, 3).inOrder();
  }

  @Test
  public void getDeckByIndex() {
    Lineup lineup = Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "C (DE/FJ)", "D (IO/SH)");
    assertThrows(RuntimeException.class, () -> lineup.getDeck(-1));
    assertThat(lineup.getDeck(0)).isEqualTo(0);
    assertThat(lineup.getDeck(1)).isEqualTo(2);
    assertThat(lineup.getDeck(2)).isEqualTo(3);
    assertThrows(RuntimeException.class, () -> lineup.getDeck(3));
  }


  @Test
  public void getDeckNameByIndex() {
    Lineup lineup = Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "C (DE/FJ)", "D (IO/SH)");
    assertThrows(RuntimeException.class, () -> lineup.getDeckName(-1));
    assertThat(lineup.getDeckName(0)).isEqualTo("A (IO/NX)");
    assertThat(lineup.getDeckName(1)).isEqualTo("C (DE/FJ)");
    assertThat(lineup.getDeckName(2)).isEqualTo("D (IO/SH)");
    assertThrows(RuntimeException.class, () -> lineup.getDeckName(3));
  }

  @Test
  public void isValid_true() {
    assertThat(Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "C (DE/FJ)", "D (IO/SH)").isValid())
        .isTrue();
  }

  @Test
  public void isValid_false() {
    assertThat(Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "C (DE/FJ)", "A (IO/NX)").isValid())
        .isFalse();
    assertThat(Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "C (DE/FJ)", "B (IO/NX)").isValid())
        .isFalse();
    assertThat(
        Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "C (DE/FJ)", "Z/A/B (IO/FJ)").isValid())
        .isFalse();
  }

  @Test
  public void obeysEqualsAndHashcode() {
    new EqualsTester()
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX), Lineup.ofDeckNames(MATCHUP_MATRIX))
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX, 0),
            Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)"))
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX, 1),
            Lineup.ofDeckNames(MATCHUP_MATRIX, "B (IO/NX)"))
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1),
            Lineup.ofDeckNames(MATCHUP_MATRIX, "A (IO/NX)", "B (IO/NX)"))
        .testEquals();
  }

  @Test
  public void reset_clearsMetadataFields() {
    Lineup lineup = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1);
    lineup.metadata()
        .incrementPlayedAgainst(1)
        .incrementPlayedAgainst(1)
        .incrementBanned(1).applyMatchup(Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 3), 0.2, 1);

    assertThat(lineup.resetMetadata()).isEqualTo(Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1));
  }

  @Test
  public void copy_createsEqualLineup() {
    Lineup lineup = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1);

    Lineup copy = lineup.copy();

    assertThat(copy).isNotSameInstanceAs(lineup);
    assertThat(copy.metadata()).isNotSameInstanceAs(lineup.metadata());
    assertThat(copy).isEqualTo(lineup);
  }

  @Test
  public void copy_createsSeparateMetadata() {
    Lineup lineup = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1);

    Lineup copy = lineup.copy();
    lineup.metadata().incrementBanned(0);

    assertThat(copy).isNotSameInstanceAs(lineup);
    assertThat(copy.metadata()).isNotSameInstanceAs(lineup.metadata());
    assertThat(copy.metadata()).isNotEqualTo(lineup.metadata());
  }
}