package com.redpup.bracketbuster.sim;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.sim.Calculations.winRateBestTwoOfThreeOneBanNaive;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.model.Matchups;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.util.WeightedDoubleMetric;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(JUnit4.class)
public final class RunnerTest {

  private static final double ERROR = 1.0e-8;

  private static final MatchupMessage MATCHUP_MESSAGE_A_A
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("A")
      .setWins(1)
      .setGames(2)
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

  private static final MatchupMessage MATCHUP_MESSAGE_A_1
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("1")
      .setWins(4)
      .setGames(7)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_2
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("2")
      .setWins(5)
      .setGames(7)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_3
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("3")
      .setWins(2)
      .setGames(7)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_B
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("B")
      .setWins(2)
      .setGames(5)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_C
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("C")
      .setWins(4)
      .setGames(7)
      .build();

  private static final MatchupMessage MATCHUP_MESSAGE_B_1
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("1")
      .setWins(4)
      .setGames(11)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_2
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("2")
      .setWins(5)
      .setGames(11)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_3
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("3")
      .setWins(10)
      .setGames(11)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_B_C
      = MatchupMessage.newBuilder()
      .setPlayer("B")
      .setOpponent("C")
      .setWins(3)
      .setGames(11)
      .build();

  private static final MatchupMessage MATCHUP_MESSAGE_C_1
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("1")
      .setWins(13)
      .setGames(17)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_2
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("2")
      .setWins(15)
      .setGames(17)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_3
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("3")
      .setWins(3)
      .setGames(17)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_C_Z
      = MatchupMessage.newBuilder()
      .setPlayer("C")
      .setOpponent("Z")
      .setWins(1)
      .setGames(17)
      .build();

  private static final MatchupMatrix MATRIX = MatchupMatrix.from(ImmutableList.of(
      MATCHUP_MESSAGE_A_A,
      MATCHUP_MESSAGE_B_B,
      MATCHUP_MESSAGE_C_C,
      MATCHUP_MESSAGE_A_1,
      MATCHUP_MESSAGE_A_2,
      MATCHUP_MESSAGE_A_3,
      MATCHUP_MESSAGE_A_B,
      MATCHUP_MESSAGE_A_C,
      MATCHUP_MESSAGE_B_1,
      MATCHUP_MESSAGE_B_2,
      MATCHUP_MESSAGE_B_3,
      MATCHUP_MESSAGE_B_C,
      MATCHUP_MESSAGE_C_1,
      MATCHUP_MESSAGE_C_2,
      MATCHUP_MESSAGE_C_3,
      MATCHUP_MESSAGE_C_Z,
      Matchups.inverse(MATCHUP_MESSAGE_A_1),
      Matchups.inverse(MATCHUP_MESSAGE_A_2),
      Matchups.inverse(MATCHUP_MESSAGE_A_3),
      Matchups.inverse(MATCHUP_MESSAGE_A_B),
      Matchups.inverse(MATCHUP_MESSAGE_A_C),
      Matchups.inverse(MATCHUP_MESSAGE_B_1),
      Matchups.inverse(MATCHUP_MESSAGE_B_2),
      Matchups.inverse(MATCHUP_MESSAGE_B_3),
      Matchups.inverse(MATCHUP_MESSAGE_B_C),
      Matchups.inverse(MATCHUP_MESSAGE_C_1),
      Matchups.inverse(MATCHUP_MESSAGE_C_2),
      Matchups.inverse(MATCHUP_MESSAGE_C_3),
      Matchups.inverse(MATCHUP_MESSAGE_C_Z)
  ), ImmutableList.of(), ImmutableMap.of());

  private final Lineup player = Lineup.ofDeckNames(MATRIX, "A", "B", "C");
  private final Lineup opponent1 = Lineup.ofDeckNames(MATRIX, "1", "2", "3");
  private final Lineup opponent2 = Lineup.ofDeckNames(MATRIX, "1", "A", "B");
  private final Lineup opponent3 = Lineup.ofDeckNames(MATRIX, "2", "3", "C");
  private final Lineup missingMatchupPlayer = Lineup.ofDeckNames(MATRIX, "1", "A", "Z");

  @Rule
  public final MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private Logger logger;

  private Runner runner;

  @Before
  public void setup() {
    runner = Runner.builder()
        .setMatchupMatrix(MATRIX)
        .setPruneRatios(ImmutableList.of(0.5, 0.1, 0.0))
        .setLogger(logger)
        .build();

    player.resetMetadata();
    opponent1.resetMetadata();
    opponent2.resetMetadata();
    opponent3.resetMetadata();
  }

  @Test
  public void computeMatchupWinRate() {
    assertThat(runner.computeMatchupWinRate(player, opponent1))
        .isEqualTo(winRateBestTwoOfThreeOneBanNaive(player, opponent1, MATRIX));
    verify(logger).handleMatchup();
  }

  @Test
  public void computeWeightedWinRate() {
    WeightedDoubleMetric weightedDoubleMetric
        = runner.computeTotalWinRate(player, ImmutableMap.of(opponent1,
        0.1, opponent2, 0.2, opponent3, 0.3, missingMatchupPlayer, 0.4));

    verify(logger, times(3)).handleMatchup();

    double winRate1 = runner.computeMatchupWinRate(player, opponent1);
    double winRate2 = runner.computeMatchupWinRate(player, opponent2);
    double winRate3 = runner.computeMatchupWinRate(player, opponent3);

    assertThat(weightedDoubleMetric.getWeightedMean())
        .isWithin(ERROR).of((winRate1 * 0.1 + winRate2 * 0.2 + winRate3 * 0.3) / 0.6);
    assertThat(weightedDoubleMetric.getUnweightedMean())
        .isWithin(ERROR).of((winRate1 + winRate2 + winRate3) / 3.0);
    assertThat(weightedDoubleMetric.getMedian())
        .isEqualTo(runner.computeMatchupWinRate(player, opponent1));

    assertThat(player.metadata().getBestMatchups())
        .containsExactly(
            opponent3, winRate3,
            opponent1, winRate1,
            opponent2, winRate2)
        .inOrder();
    assertThat(player.metadata().getWorstMatchups())
        .containsExactly(
            opponent2, winRate2,
            opponent1, winRate1,
            opponent3, winRate3)
        .inOrder();

  }
}