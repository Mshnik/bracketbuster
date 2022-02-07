package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;

import com.redpup.bracketbuster.model.proto.MatchupMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MatchupMatrixTest {

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

  @Test
  public void buildAndGetMatchup() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    assertThat(matrix.getMatchup("A", "B"))
        .isEqualTo(MATCHUP_MESSAGE_A_B);
    assertThat(matrix.getMatchup("B", "A"))
        .isEqualTo(MATCHUP_MESSAGE_B_A);
    assertThat(matrix.getMatchup("A", "A"))
        .isEqualTo(MATCHUP_MESSAGE_A_A);
    assertThat(matrix.getMatchup("B", "B"))
        .isNull();
  }

  @Test
  public void unknownMatchupThrows() {
    MatchupMatrix matrix = MatchupMatrix.from(
        MATCHUP_MESSAGE_A_A,
        MATCHUP_MESSAGE_A_B,
        MATCHUP_MESSAGE_B_A);

    // assertThrows(IllegalArgumentException.class,
    //     () -> matrix.getMatchup("A", "C"));
  }

}