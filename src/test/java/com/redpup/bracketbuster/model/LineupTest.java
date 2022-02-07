package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.testing.EqualsTester;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LineupTest {

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
  private static final MatchupMessage MATCHUP_MESSAGE_A_A
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("A")
      .setWins(2)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_C
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("C")
      .setWins(1)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_A
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("A")
      .setWins(3)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_D
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("D")
      .setWins(1)
      .setGames(5)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_D_A
      = MatchupMessage.newBuilder()
      .setPlayer("D")
      .setOpponent("A")
      .setWins(4)
      .setGames(5)
      .build();

  private static final MatchupMatrix MATCHUP_MATRIX
      = MatchupMatrix
      .from(MATCHUP_MESSAGE_A_A, MATCHUP_MESSAGE_A_B, MATCHUP_MESSAGE_A_C, MATCHUP_MESSAGE_A_D,
          MATCHUP_MESSAGE_B_A, MATCHUP_MESSAGE_C_A, MATCHUP_MESSAGE_D_A);


  @Test
  public void buildLineup_fromIndices() {
    Lineup lineup = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 2, 3);
    assertThat(lineup.getDecks()).containsExactly(0, 2, 3).inOrder();
    assertThat(lineup.getDeckNames()).containsExactly("A", "C", "D").inOrder();
  }

  @Test
  public void buildLineup_fromNames() {
    Lineup lineup = Lineup.ofDeckNames(MATCHUP_MATRIX, "A", "C", "D");
    assertThat(lineup.getDeckNames()).containsExactly("A", "C", "D").inOrder();
    assertThat(lineup.getDecks()).containsExactly(0, 2, 3).inOrder();
  }

  @Test
  public void getDeckByIndex() {
    Lineup lineup = Lineup.ofDeckNames(MATCHUP_MATRIX, "A", "C", "D");
    assertThrows(RuntimeException.class, () -> lineup.getDeck(-1));
    assertThat(lineup.getDeck(0)).isEqualTo(0);
    assertThat(lineup.getDeck(1)).isEqualTo(2);
    assertThat(lineup.getDeck(2)).isEqualTo(3);
    assertThrows(RuntimeException.class, () -> lineup.getDeck(3));
  }


  @Test
  public void getDeckNameByIndex() {
    Lineup lineup = Lineup.ofDeckNames(MATCHUP_MATRIX, "A", "C", "D");
    assertThrows(RuntimeException.class, () -> lineup.getDeckName(-1));
    assertThat(lineup.getDeckName(0)).isEqualTo("A");
    assertThat(lineup.getDeckName(1)).isEqualTo("C");
    assertThat(lineup.getDeckName(2)).isEqualTo("D");
    assertThrows(RuntimeException.class, () -> lineup.getDeckName(3));
  }

  @Test
  public void obeysEqualsAndHashcolde() {
    new EqualsTester()
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX), Lineup.ofDeckNames(MATCHUP_MATRIX))
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX, 0),
            Lineup.ofDeckNames(MATCHUP_MATRIX, "A"))
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX, 1),
            Lineup.ofDeckNames(MATCHUP_MATRIX, "B"))
        .addEqualityGroup(Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1),
            Lineup.ofDeckNames(MATCHUP_MATRIX, "A", "B"))
        .testEquals();
  }

}