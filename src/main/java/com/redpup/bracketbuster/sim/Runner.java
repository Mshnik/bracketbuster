package com.redpup.bracketbuster.sim;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.redpup.bracketbuster.sim.Calculations.winRateBestTwoOfThreeOneBanNaive;
import static com.redpup.bracketbuster.sim.Calculations.winRateBestTwoOfThreeOneBanNash;
import static com.redpup.bracketbuster.sim.Output.buildOutput;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.LineupWeightType;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.model.Matchups;
import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.sim.Calculations.CalculationType;
import com.redpup.bracketbuster.util.Pair;
import com.redpup.bracketbuster.util.WeightedDoubleMetric;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Top level executable runner class for running the bracketbuster.
 *
 * <p>Parameterizable along certain axes. See {@link Builder} for settable parameters.
 */
@AutoValue
public abstract class Runner {

  /**
   * Executes this runner.
   *
   * <p>Parameters to the run can be set on the builder below, before building and running.
   */
  public static void main(String[] args) throws Exception {
    Path matchupsFilePath = Paths.get("src", "main", "resources", "stats_new.csv");
    builder()
        .setCalculationType(CalculationType.NAIVE)
        .setLineupWeightType(LineupWeightType.GEOMETRIC)
        .setSortType(SortType.WEIGHTED_MEAN_WIN_RATE)
        .setMatchupMatrixFromFile(matchupsFilePath)
        .setPruneRatios(ImmutableList.of(0.0))
        .setTopKToPrintLimit(80)
        .build()
        .run();
  }

  Runner() {
  }

  /**
   * Returns a new {@link Builder}.
   */
  public static Builder builder() {
    return new com.redpup.bracketbuster.sim.AutoValue_Runner.Builder()
        .setCalculationType(CalculationType.NAIVE)
        .setSortType(SortType.UNWEIGHTED_MEAN_WIN_RATE)
        .setLineupWeightType(LineupWeightType.AVERAGE)
        .setPruneRatios(ImmutableList.of(0.0))
        .setTopKToPrintLimit(25)
        .setLogger(new SystemPrintLogger());
  }

  /**
   * The configured matchupMatrix() to use within this runner.
   */
  abstract MatchupMatrix matchupMatrix();

  /**
   * All valid player lineups within {@link #matchupMatrix()}.
   */
  final List<Lineup> allPlayerLineups() {
    return matchupMatrix().createAllValidPlayerLineups();
  }

  /**
   * All valid opponent lineups within {@link #matchupMatrix()} weighted by lineup play rate.
   */
  final Map<Lineup, Double> allWeightedOpponentLineups() {
    return matchupMatrix().createWeightedValidOpponentLineups(lineupWeightType());
  }

  /**
   * How to sort lineups after a round of play.
   */
  abstract SortType sortType();

  /**
   * Ratios of the domain to prune down to after each round.
   *
   * <p>This should be read as "After round X, prune down to the top X% of remaining Lineups." The
   * last element should always be 0.
   */
  abstract ImmutableList<Double> pruneRatios();

  /**
   * Returns the {@link #pruneRatios()} at the given {@code index}.
   */
  final double pruneRatio(int index) {
    return pruneRatios().get(index);
  }

  /**
   * Number of best lineups to print stats for when outputting.
   */
  abstract int topKToPrintLimit();

  /**
   * How to apply calculations.
   */
  abstract CalculationType calculationType();

  /**
   * How to compute lineup weights. Only used in {@link SortType#WEIGHTED_MEAN_WIN_RATE}.
   */
  abstract LineupWeightType lineupWeightType();

  /**
   * Handler for logs and other UI updates while running a simulation.
   */
  abstract Logger logger();

  /**
   * Converts this runner back into a {@link Builder}.
   */
  abstract Builder toBuilder();

  /**
   * Builder class for {@link Runner}.
   */
  @AutoValue.Builder
  public static abstract class Builder {

    Builder() {
    }

    /**
     * Sets {@link #matchupMatrix()}.
     */
    public abstract Builder setMatchupMatrix(MatchupMatrix matchupMatrix);

    /**
     * Sets {@link #matchupMatrix()} from the given {@code filePath}.
     */
    public final Builder setMatchupMatrixFromFile(Path filePath) throws IOException {
      MatchupList list = Matchups.readMatchupListFromCsv(filePath);
      return setMatchupMatrix(MatchupMatrix.fromProto(list));
    }

    /**
     * Sets {@link #sortType()}.
     */
    public abstract Builder setSortType(SortType sortType);

    /**
     * Sets {@link #pruneRatios()}.
     */
    public abstract Builder setPruneRatios(ImmutableList<Double> pruneRatios);

    /**
     * Sets {@link #topKToPrintLimit()}.
     */
    public abstract Builder setTopKToPrintLimit(int limit);

    /**
     * Sets {@link #calculationType()}.
     */
    public abstract Builder setCalculationType(CalculationType calculationType);

    /**
     * Sets {@link #lineupWeightType()}.
     */
    public abstract Builder setLineupWeightType(LineupWeightType lineupWeightType);

    /**
     * Sets {@link #logger()}.
     */
    public abstract Builder setLogger(Logger logger);

    /**
     * Builds this into a {@link Runner}.
     */
    public abstract Runner build();
  }

  /**
   * Executes this runner.
   */
  public void run() {
    logger().log(String.format("Matchups contains %d decks.", matchupMatrix().getNumDecks()));
    computeTopLineupsAgainstEveryone();
  }

  /**
   * Computes the top player {@link Lineup}s against every possible opponent lineup. Output is
   * streamed into {@link #logger()}.
   */
  @VisibleForTesting
  void computeTopLineupsAgainstEveryone() {
    final Set<Lineup> playerLineups = new HashSet<>(allPlayerLineups());
    final Map<Lineup, Double> opponentLineups = new HashMap<>(allWeightedOpponentLineups());
    logger().log(String
        .format("Created %d player lineups and %d opponent lineups.", playerLineups.size(),
            opponentLineups.size()));

    int originalSize = playerLineups.size();
    for (int i = 0; i < pruneRatios().size(); i++) {
      logger().setIteration(i);
      // Order all lineups by winrate against the current set of lineups.
      logger().setCurrentStep("Computing Lineup Win Rates");
      ImmutableList<Pair<Lineup, WeightedDoubleMetric>> playersByWinRateMetric =
          playerLineups.stream()
              .map(p -> Pair.of(p, computeTotalWinRate(p, opponentLineups)))
              .sorted(Comparator.comparing(Pair::second, sortType().comparator))
              .collect(toImmutableList());

      // Handle output through logger.
      logger().setCurrentStep("Handling Output");
      logger().handleOutput(
          buildOutput(
              playersByWinRateMetric.stream().collect(Pair.toImmutableMap()),
              matchupMatrix(),
              sortType(),
              topKToPrintLimit()),
          this);

      // Prune lineups for next iteration, if there is a next iteration.
      if (i < pruneRatios().size() - 1) {
        // TODO: This is sorta broken now that player and opponent lineups are separated,
        // as opponent lineups are no longer pruned here. This is ok as we are no longer using
        // the prune functionality, but if we do this will have to be fixed.
        playerLineups.retainAll(
            playersByWinRateMetric.stream()
                .limit((long) (originalSize * pruneRatio(i)))
                .map(Pair::first)
                .collect(toImmutableSet()));
      }
    }
  }

  /**
   * Computes the total weighted win rate of {@code player} against {@code allPlayers}.
   *
   * <p>Metadata collected along the way are stored in {@link Lineup#metadata()}.
   */
  @VisibleForTesting
  @CanIgnoreReturnValue
  WeightedDoubleMetric computeTotalWinRate(Lineup player,
      Map<Lineup, Double> allPlayersWithWeights) {
    player.resetMetadata();

    for (Map.Entry<Lineup, Double> opponent : allPlayersWithWeights.entrySet()) {
      if (matchupMatrix().canPlay(player, opponent.getKey())) {
        player.metadata()
            .applyMatchup(opponent.getKey(), computeMatchupWinRate(player, opponent.getKey()),
                opponent.getValue());
      }
    }

    return player.metadata().getWinRateMetric();
  }

  /**
   * Computes the win rate of {@code player} against {@code opponent}.
   */
  @VisibleForTesting
  double computeMatchupWinRate(Lineup player, Lineup opponent) {
    logger().handleMatchup();
    switch (calculationType()) {
      case NAIVE:
        return winRateBestTwoOfThreeOneBanNaive(player, opponent, matchupMatrix());
      case NASH:
        return winRateBestTwoOfThreeOneBanNash(player, opponent, matchupMatrix());
    }

    throw new UnsupportedOperationException("Unsupported calculationType:" + calculationType());
  }
}
