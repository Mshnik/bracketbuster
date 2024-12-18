package com.redpup.bracketbuster.sim;

import static com.redpup.bracketbuster.util.Constants.NUM_BEST_WORST_MATCHUPS;
import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

/**
 * A {@link Logger} that logs messages to {@link System#out}
 */
public final class SystemPrintLogger implements Logger {

  private int iteration;
  private long count;

  @Override
  public void log(String message) {
    System.out.println(message);
  }

  @Override
  public void setIteration(int i) {
    iteration = i;
    count = 0;
  }

  @Override
  public void setCurrentStep(String step) {
    System.out.println("==================================================");
    System.out.printf("Iteration %d: %s%n", iteration, step);
    System.out.println("==================================================");
  }

  @Override
  public void handleMatchup() {
    if (++count % 100000 == 0) {
      System.out.println("\tCalculating matchup..." + count);
    }
  }

  @Override
  public void handleOutput(Output output, Runner runner) {
    String label = iteration == 0 ? "Everyone"
        : String.format("Top %.0f%%", runner.pruneRatio(iteration - 1) * 100);

    System.out.println(">>--------------------");

    System.out.printf("[%s] Best %d lineups:%n", label, runner.topKToPrintLimit());

    System.out.printf(
        "Deck1,Deck2,Deck3,WinRate(WeightedMean),WinRate(UnweightedMean),WinRate(UnweightedMedian),Best Matchups%sWorstMatchups%sBans %%%n",
        Stream.generate(() -> ",").limit(NUM_BEST_WORST_MATCHUPS * 2).collect(joining()),
        Stream.generate(() -> ",").limit(NUM_BEST_WORST_MATCHUPS * 2 + 1).collect(joining()));

    output.topLineups.entrySet().stream()
        .map(
            p -> String.format("%s,%s,%s,%.5f,%.5f,%.5f,%s,%s",
                p.getKey().getDeckName(0),
                p.getKey().getDeckName(1),
                p.getKey().getDeckName(2),
                p.getValue().getWeightedMean(),
                p.getValue().getUnweightedMean(),
                p.getValue().getMedian(),
                p.getKey().metadata().toBestAndWorstMatchupsString(),
                p.getKey().metadata().toBanPercentString(runner.matchupMatrix())))
        .forEach(System.out::println);

    System.out.println(">>--------------------");

    System.out.printf("[%s] Deck Meta Percent:%n", label);
    System.out.println("Deck,Meta %");

    output.metaCompPercent.entrySet().stream()
        .map(p -> String.format("%s,%f", p.getKey(), p.getValue()))
        .forEach(System.out::println);

    System.out.println(">>--------------------");
  }
}
