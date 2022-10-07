package com.redpup.bracketbuster.sim;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.sim.Calculations.winRateBestTwoOfThreeOneBanNaive;
import static com.redpup.bracketbuster.sim.Calculations.winRateBestTwoOfThreeOneBanNash;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.sim.Calculations.CalculationType;
import com.redpup.bracketbuster.util.Pair;
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

  private static final MatchupMatrix MATRIX = MatchupMatrix.from(
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
      MATCHUP_MESSAGE_C_Z);

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
  public void computeBestAndWorstMatchups_empty() {
    runner.computeBestAndWorstMatchups(player, ImmutableList.of());

    assertThat(player.metadata().getBestMatchups()).isEmpty();
    assertThat(player.metadata().getWorstMatchups()).isEmpty();

    verify(logger, never()).handleMatchup();
  }

  @Test
  public void computeBestAndWorstMatchups_skipsLineupMissingMatchupData() {
    runner.computeBestAndWorstMatchups(player, ImmutableList.of(missingMatchupPlayer));

    assertThat(player.metadata().getBestMatchups()).isEmpty();
    assertThat(player.metadata().getWorstMatchups()).isEmpty();

    verify(logger, never()).handleMatchup();
  }

  @Test
  public void computeBestAndWorstMatchups_handlesLineup_naive() {
    runner.toBuilder().setCalculationType(CalculationType.NAIVE).build()
        .computeBestAndWorstMatchups(player, ImmutableList.of(opponent1));

    assertThat(player.metadata().getBestMatchups())
        .containsExactly(opponent1, winRateBestTwoOfThreeOneBanNaive(player, opponent1, MATRIX))
        .inOrder();
    assertThat(player.metadata().getWorstMatchups())
        .containsExactly(opponent1, winRateBestTwoOfThreeOneBanNaive(player, opponent1, MATRIX))
        .inOrder();

    verify(logger, times(1)).handleMatchup();
  }

  @Test
  public void computeBestAndWorstMatchups_handlesManyLineups_naive() {
    runner.toBuilder().setCalculationType(CalculationType.NAIVE).build()
        .computeBestAndWorstMatchups(player, ImmutableList.of(opponent1, opponent2, opponent3));

    assertThat(player.metadata().getBestMatchups())
        .containsExactly(
            opponent3, winRateBestTwoOfThreeOneBanNaive(player, opponent3, MATRIX),
            opponent1, winRateBestTwoOfThreeOneBanNaive(player, opponent1, MATRIX),
            opponent2, winRateBestTwoOfThreeOneBanNaive(player, opponent2, MATRIX))
        .inOrder();
    assertThat(player.metadata().getWorstMatchups())
        .containsExactly(
            opponent2, winRateBestTwoOfThreeOneBanNaive(player, opponent2, MATRIX),
            opponent1, winRateBestTwoOfThreeOneBanNaive(player, opponent1, MATRIX),
            opponent3, winRateBestTwoOfThreeOneBanNaive(player, opponent3, MATRIX))
        .inOrder();

    verify(logger, times(3)).handleMatchup();
  }

  @Test
  public void computeBestAndWorstMatchupsWithWinRates_naive() {
    // WinRates are unused.
    runner.toBuilder().setCalculationType(CalculationType.NAIVE).build()
        .computeBestAndWorstMatchupsWithWinRates(player, ImmutableList.of(
            Pair.of(opponent1, 100.0), Pair.of(opponent2, 200.0), Pair.of(opponent3, 300.0)));

    assertThat(player.metadata().getBestMatchups())
        .containsExactly(
            opponent3, winRateBestTwoOfThreeOneBanNaive(player, opponent3, MATRIX),
            opponent1, winRateBestTwoOfThreeOneBanNaive(player, opponent1, MATRIX),
            opponent2, winRateBestTwoOfThreeOneBanNaive(player, opponent2, MATRIX))
        .inOrder();
    assertThat(player.metadata().getWorstMatchups())
        .containsExactly(
            opponent2, winRateBestTwoOfThreeOneBanNaive(player, opponent2, MATRIX),
            opponent1, winRateBestTwoOfThreeOneBanNaive(player, opponent1, MATRIX),
            opponent3, winRateBestTwoOfThreeOneBanNaive(player, opponent3, MATRIX))
        .inOrder();

    verify(logger, times(3)).handleMatchup();
  }

  @Test
  public void computeBestAndWorstMatchups_handlesLineup_nash() {
    runner.toBuilder().setCalculationType(CalculationType.NASH).build()
        .computeBestAndWorstMatchups(player, ImmutableList.of(opponent1));

    assertThat(player.metadata().getBestMatchups())
        .containsExactly(opponent1, winRateBestTwoOfThreeOneBanNash(player, opponent1, MATRIX))
        .inOrder();
    assertThat(player.metadata().getWorstMatchups())
        .containsExactly(opponent1, winRateBestTwoOfThreeOneBanNash(player, opponent1, MATRIX))
        .inOrder();

    verify(logger, times(1)).handleMatchup();
  }

  @Test
  public void computeBestAndWorstMatchups_handlesManyLineups_nash() {
    runner.toBuilder().setCalculationType(CalculationType.NASH).build()
        .computeBestAndWorstMatchups(player, ImmutableList.of(opponent1, opponent2, opponent3));

    assertThat(player.metadata().getBestMatchups())
        .containsExactly(
            opponent1, winRateBestTwoOfThreeOneBanNash(player, opponent1, MATRIX),
            opponent2, winRateBestTwoOfThreeOneBanNash(player, opponent2, MATRIX),
            opponent3, winRateBestTwoOfThreeOneBanNash(player, opponent3, MATRIX))
        .inOrder();
    assertThat(player.metadata().getWorstMatchups())
        .containsExactly(
            opponent3, winRateBestTwoOfThreeOneBanNash(player, opponent3, MATRIX),
            opponent2, winRateBestTwoOfThreeOneBanNash(player, opponent2, MATRIX),
            opponent1, winRateBestTwoOfThreeOneBanNash(player, opponent1, MATRIX))
        .inOrder();

    verify(logger, times(3)).handleMatchup();
  }

  @Test
  public void computeBestAndWorstMatchupsWithWinRates_nash() {
    // WinRates are unused.
    runner.toBuilder().setCalculationType(CalculationType.NASH).build()
        .computeBestAndWorstMatchupsWithWinRates(player, ImmutableList.of(
            Pair.of(opponent1, 100.0), Pair.of(opponent2, 200.0), Pair.of(opponent3, 300.0)));

    assertThat(player.metadata().getBestMatchups())
        .containsExactly(
            opponent1, winRateBestTwoOfThreeOneBanNash(player, opponent1, MATRIX),
            opponent2, winRateBestTwoOfThreeOneBanNash(player, opponent2, MATRIX),
            opponent3, winRateBestTwoOfThreeOneBanNash(player, opponent3, MATRIX))
        .inOrder();
    assertThat(player.metadata().getWorstMatchups())
        .containsExactly(
            opponent3, winRateBestTwoOfThreeOneBanNash(player, opponent3, MATRIX),
            opponent2, winRateBestTwoOfThreeOneBanNash(player, opponent2, MATRIX),
            opponent1, winRateBestTwoOfThreeOneBanNash(player, opponent1, MATRIX))
        .inOrder();

    verify(logger, times(3)).handleMatchup();
  }


}