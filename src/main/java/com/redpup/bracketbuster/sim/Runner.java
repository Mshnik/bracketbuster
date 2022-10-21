package com.redpup.bracketbuster.sim;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Lists.transform;
import static com.redpup.bracketbuster.sim.Calculations.winRateBestTwoOfThreeOneBanNaive;
import static com.redpup.bracketbuster.sim.Calculations.winRateBestTwoOfThreeOneBanNash;
import static com.redpup.bracketbuster.sim.Output.buildOutput;
import static java.lang.Math.min;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.model.Matchups;
import com.redpup.bracketbuster.model.proto.MatchupList;
import com.redpup.bracketbuster.sim.Calculations.CalculationType;
import com.redpup.bracketbuster.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Path matchupsFilePath = Paths.get("src", "main", "resources", "stats.csv");
    builder()
        .setCalculationType(CalculationType.NASH)
        .setMatchupMatrixFromFile(matchupsFilePath)
        .setPruneRatios(ImmutableList.of(0.7, 0.5, 0.2, 0.1, 0.001, 0.0))
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
        .setSortType(SortType.WIN_RATE)
        .setPruneRatios(ImmutableList.of(0.0))
        .setTopKToReceiveBestAndWorstMatchupAnalysis(25)
        .setTopKToParticipateInBestAndWorstMatchupAnalysis(100)
        .setLogger(new SystemPrintLogger());
  }

  /**
   * The configured matchupMatrix() to use within this runner.
   */
  abstract MatchupMatrix matchupMatrix();

  /**
   * All valid lineups within {@link #matchupMatrix()}.
   */
  final List<Lineup> allLineups() {
    return matchupMatrix().createAllValidLineups();
  }

  /**
   * All valid lineups within {@link #matchupMatrix()} weighted by lineup play rate.
   */
  final Map<Lineup, Double> weightedLineups() {
    return matchupMatrix().createWeightedValidLineups();
  }

  /**
   * How to sort lineups after a round of play.
   */
  enum SortType {
    /**
     * Sort lineups by win rate, descending.
     */
    WIN_RATE,
    /**
     * Sort lineups by _weighted_ win rate, descending.
     */
    WEIGHTED_WIN_RATE;
  }

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
   * Number of lineups to receive best and worst matchup analysis.
   */
  abstract int topKToReceiveBestAndWorstMatchupAnalysis();

  /**
   * Number of lineups to participate as opponents in best and worst matchup analysis..
   */
  abstract int topKToParticipateInBestAndWorstMatchupAnalysis();

  /**
   * How to apply calculations.
   */
  abstract CalculationType calculationType();

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
     * Sets {@link #topKToReceiveBestAndWorstMatchupAnalysis()}.
     */
    public abstract Builder setTopKToReceiveBestAndWorstMatchupAnalysis(int k);

    /**
     * Sets {@link #topKToParticipateInBestAndWorstMatchupAnalysis()}.
     */
    public abstract Builder setTopKToParticipateInBestAndWorstMatchupAnalysis(int k);

    /**
     * Sets {@link #calculationType()}.
     */
    public abstract Builder setCalculationType(CalculationType calculationType);

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
   * Computes the top {@link Lineup}s against every possible lineup. Output is streamed into {@link
   * #logger()}.
   */
  @VisibleForTesting
  void computeTopLineupsAgainstEveryone() {
    final Map<Lineup, Double> lineups = new HashMap<>(weightedLineups());
    int originalSize = lineups.size();
    logger().log(String.format("Created %d lineups.", originalSize));

    for (int i = 0; i < pruneRatios().size(); i++) {
      logger().setIteration(i);
      // Order all lineups by winrate against the current set of lineups.
      logger().setCurrentStep("Computing All Lineup Win Rates");
      ImmutableList<Pair<Lineup, Double>> playersByWinRate =
          lineups.keySet().stream()
              .map(p -> Pair.of(p, computeWinRate(p, lineups)))
              .sorted(Pair.<Lineup>rightDoubleComparator().reversed())
              .collect(toImmutableList());

      // Extract the top k lineups from this round for matchup analysis.
      ImmutableList<Pair<Lineup, Double>> topKPlayersForMatchups =
          playersByWinRate.stream()
              .limit(topKToParticipateInBestAndWorstMatchupAnalysis())
              .collect(toImmutableList());

      // Apply topK players for matchup analysis to everyone.
      logger().setCurrentStep("Computing Best And Worst Matchup Analysis");
      ImmutableList<Pair<Lineup, Double>> topKPlayersForAnalysis =
          playersByWinRate
              .subList(0, min(playersByWinRate.size(), topKToReceiveBestAndWorstMatchupAnalysis()));

      topKPlayersForAnalysis
          .forEach(
              p -> computeBestAndWorstMatchupsWithWinRates(p.first(), topKPlayersForMatchups));

      logger().setCurrentStep("Handling Output");
      logger().handleOutput(
          buildOutput(
              playersByWinRate.stream().collect(Pair.toImmutableMap()),
              matchupMatrix(),
              topKToReceiveBestAndWorstMatchupAnalysis()),
          this);

      // Prune lineups for next iteration, if there is a next iteration.
      if (i < pruneRatios().size() - 1) {
        lineups.keySet().retainAll(
            playersByWinRate.stream()
                .limit((long) (originalSize * pruneRatio(i)))
                .map(Pair::first)
                .collect(toImmutableSet()));
      }
    }
  }

  /**
   * Computes the total win rate of {@code player} against {@code allPlayersWithWeights} subject to
   * {@link #sortType()}.
   */
  private double computeWinRate(Lineup player, Map<Lineup, Double> allPlayersWithWeights) {
    switch (sortType()) {
      case WIN_RATE:
        return computeTotalWinRate(player, allPlayersWithWeights.keySet());
      case WEIGHTED_WIN_RATE:
        return computeTotalWeightedWinRate(player, allPlayersWithWeights);
    }

    throw new UnsupportedOperationException("Unsupported sortType: " + sortType());
  }

  /**
   * Computes the total win rate of {@code player} against {@code allPlayers}.
   *
   * <p>Metadata collected along the way are stored in {@link Lineup#metadata()}.
   */
  @VisibleForTesting
  double computeTotalWinRate(Lineup player, Collection<Lineup> allPlayers) {
    player.resetMetadata();

    double winRate = 0;
    int numMatches = 0;

    for (Lineup opponent : allPlayers) {
      if (matchupMatrix().canPlay(player, opponent)) {
        winRate += computeMatchupWinRate(player, opponent);
        numMatches++;
      }
    }

    if (numMatches > 0) {
      return winRate / numMatches;
    } else {
      return 0;
    }
  }

  /**
   * Computes the total weighted win rate of {@code player} against {@code allPlayers}.
   *
   * <p>Metadata collected along the way are stored in {@link Lineup#metadata()}.
   */
  @VisibleForTesting
  double computeTotalWeightedWinRate(Lineup player, Map<Lineup, Double> allPlayersWithWeights) {
    player.resetMetadata();

    double winRate = 0;
    double weightSum = 0;

    for (Map.Entry<Lineup, Double> opponent : allPlayersWithWeights.entrySet()) {
      if (matchupMatrix().canPlay(player, opponent.getKey())) {
        winRate += computeMatchupWinRate(player, opponent.getKey()) * opponent.getValue();
        weightSum += opponent.getValue();
      }
    }

    if (weightSum > 0) {
      return winRate / weightSum;
    } else {
      return 0;
    }
  }

  /**
   * Computes the best and worst matchups for {@code player} against the given {@code topKPlayers}.
   */
  @VisibleForTesting
  void computeBestAndWorstMatchupsWithWinRates(Lineup player,
      List<? extends Pair<Lineup, ?>> topKPlayers) {
    computeBestAndWorstMatchups(player, transform(topKPlayers, Pair::first));
  }

  /**
   * Computes the best and worst matchups for {@code player} against the given {@code topKPlayers}.
   */
  @VisibleForTesting
  void computeBestAndWorstMatchups(Lineup player,
      List<Lineup> topKPlayers) {
    topKPlayers.stream()
        .filter(opponent -> matchupMatrix().canPlay(player, opponent))
        .forEach(opponent ->
            player.metadata().applyMatchup(opponent,
                computeMatchupWinRate(player, opponent)));
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
