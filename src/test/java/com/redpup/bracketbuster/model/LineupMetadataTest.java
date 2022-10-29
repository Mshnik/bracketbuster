package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.testing.EqualsTester;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LineupMetadataTest {

  private static final double ERROR = 1.0e-8;

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
      MATCHUP_MESSAGE_A_A,
      MATCHUP_MESSAGE_A_B,
      MATCHUP_MESSAGE_A_C,
      MATCHUP_MESSAGE_A_D,
      MATCHUP_MESSAGE_B_A,
      MATCHUP_MESSAGE_C_A,
      MATCHUP_MESSAGE_D_A,
      MATCHUP_MESSAGE_ZAA_B,
      MATCHUP_MESSAGE_ZAB_B);

  private static final Lineup LINEUP_1 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 2);
  private static final Lineup LINEUP_2 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 3);
  private static final Lineup LINEUP_3 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 4);
  private static final Lineup LINEUP_4 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 5);
  private static final Lineup LINEUP_5 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 2, 3);
  private static final Lineup LINEUP_6 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 2, 4);

  @Test
  public void empty_initializesArraysWithLength() {
    LineupMetadata metadata = new LineupMetadata(5);
    assertThat(metadata.getPlayedAgainst()).asList().containsExactly(0, 0, 0, 0, 0).inOrder();
    assertThat(metadata.getBanned()).usingTolerance(ERROR).containsExactly(0.0, 0.0, 0.0, 0.0, 0.0);
  }

  @Test
  public void incrementPlayedAgainst_incrementsValues() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.incrementPlayedAgainst(0);
    metadata.incrementPlayedAgainst(1);
    metadata.incrementPlayedAgainst(1);
    assertThat(metadata.getPlayedAgainst()).asList().containsExactly(1, 2, 0, 0, 0).inOrder();
  }


  @Test
  public void incrementPlayedAgainst_throwsIfOOB() {
    LineupMetadata metadata = new LineupMetadata(5);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementPlayedAgainst(-1));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementPlayedAgainst(10));
    assertThat(metadata.getPlayedAgainst()).asList().containsExactly(0, 0, 0, 0, 0).inOrder();
  }

  @Test
  public void incrementBanned_incrementsValues() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.incrementBanned(0);
    metadata.incrementBanned(1);
    metadata.incrementBanned(1);
    assertThat(metadata.getBanned()).usingTolerance(ERROR).containsExactly(1.0, 2.0, 0.0, 0.0, 0.0)
        .inOrder();
  }

  @Test
  public void incrementBannedByAmount_incrementsValues() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.incrementBanned(0);
    metadata.incrementBanned(1, 0.5);
    metadata.incrementBanned(1);
    assertThat(metadata.getBanned()).usingTolerance(ERROR).containsExactly(1.0, 1.5, 0.0, 0.0, 0.0)
        .inOrder();
  }

  @Test
  public void incrementBannedByAmount_roundsAmount() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.incrementBanned(0);
    metadata.incrementBanned(1, 0.000000000001);
    metadata.incrementBanned(2, 0.9999999999);
    assertThat(metadata.getBanned()).usingExactEquality().containsExactly(1.0, 0.0, 1.0, 0.0, 0.0)
        .inOrder();
  }

  @Test
  public void incrementBanned_throwsIfOOB() {
    LineupMetadata metadata = new LineupMetadata(5);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementBanned(-1));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementBanned(10));
    assertThat(metadata.getBanned()).usingTolerance(ERROR).containsExactly(0, 0, 0, 0, 0).inOrder();
  }

  @Test
  public void incrementBanned_applyMatchup_addsToBestAndWorst() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.applyMatchup(LINEUP_1, 0.5, 1);

    assertThat(metadata.getBestMatchups()).containsExactly(LINEUP_1, 0.5).inOrder();
    assertThat(metadata.getWorstMatchups()).containsExactly(LINEUP_1, 0.5).inOrder();
  }

  @Test
  public void incrementBanned_applyMatchup_addsToBestAndWorst_inOrder() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.applyMatchup(LINEUP_1, 0.5, 1);
    metadata.applyMatchup(LINEUP_2, 0.2, 1);

    assertThat(metadata.getBestMatchups()).containsExactly(LINEUP_1, 0.5, LINEUP_2, 0.2).inOrder();
    assertThat(metadata.getWorstMatchups()).containsExactly(LINEUP_2, 0.2, LINEUP_1, 0.5).inOrder();
  }

  @Test
  public void incrementBanned_applyMatchup_addsToBestAndWorst_evictsAsNecessary() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.applyMatchup(LINEUP_1, 0.1, 1);
    metadata.applyMatchup(LINEUP_2, 0.2, 1);
    metadata.applyMatchup(LINEUP_3, 0.3, 1);
    metadata.applyMatchup(LINEUP_4, 0.4, 1);
    metadata.applyMatchup(LINEUP_5, 0.5, 1);
    metadata.applyMatchup(LINEUP_6, 0.6, 1);

    assertThat(metadata.getBestMatchups())
        .containsExactly(
            LINEUP_6, 0.6,
            LINEUP_5, 0.5,
            LINEUP_4, 0.4,
            LINEUP_3, 0.3,
            LINEUP_2, 0.2)
        .inOrder();
    assertThat(metadata.getWorstMatchups())
        .containsExactly(
            LINEUP_1, 0.1,
            LINEUP_2, 0.2,
            LINEUP_3, 0.3,
            LINEUP_4, 0.4,
            LINEUP_5, 0.5)
        .inOrder();
  }

  @Test
  public void obeysEqualsAndHashcode() {
    new EqualsTester()
        .addEqualityGroup(new LineupMetadata(1), new LineupMetadata(1))
        .addEqualityGroup(new LineupMetadata(5), new LineupMetadata(5))
        .addEqualityGroup(new LineupMetadata(5).incrementPlayedAgainst(1),
            new LineupMetadata(5).incrementPlayedAgainst(1))
        .addEqualityGroup(new LineupMetadata(5).incrementBanned(1),
            new LineupMetadata(5).incrementBanned(1))
        .addEqualityGroup(new LineupMetadata(5).incrementBanned(1).applyMatchup(LINEUP_1, 0.1, 1),
            new LineupMetadata(5).incrementBanned(1).applyMatchup(LINEUP_1, 0.1, 1))
        .addEqualityGroup(new LineupMetadata(5).incrementBanned(1).applyMatchup(LINEUP_1, 0.5, 1),
            new LineupMetadata(5).incrementBanned(1).applyMatchup(LINEUP_1, 0.5, 1))
        .addEqualityGroup(new LineupMetadata(5).incrementBanned(1).applyMatchup(LINEUP_2, 0.1, 1),
            new LineupMetadata(5).incrementBanned(1).applyMatchup(LINEUP_2, 0.1, 1))
        .testEquals();
  }

  @Test
  public void reset_clearsFields() {
    LineupMetadata metadata = new LineupMetadata(2)
        .incrementPlayedAgainst(1)
        .incrementPlayedAgainst(1)
        .incrementBanned(1).applyMatchup(LINEUP_1, 0.2, 1);

    assertThat(metadata.reset()).isEqualTo(new LineupMetadata(2));
  }

  @Test
  public void copy_createsEqualMetadata() {
    LineupMetadata metadata = new LineupMetadata(2)
        .incrementPlayedAgainst(1)
        .incrementPlayedAgainst(1)
        .incrementBanned(1);

    LineupMetadata copy = metadata.copy();

    assertThat(copy).isNotSameInstanceAs(metadata);
    assertThat(copy).isEqualTo(metadata);
    assertThat(copy.hashCode()).isEqualTo(metadata.hashCode());
  }

  @Test
  public void copy_createsSeparateMetadata() {
    LineupMetadata metadata = new LineupMetadata(2)
        .incrementPlayedAgainst(1)
        .incrementPlayedAgainst(1)
        .incrementBanned(1);

    LineupMetadata copy = metadata.copy();
    metadata.incrementBanned(1);

    assertThat(copy).isNotEqualTo(metadata);
  }
}