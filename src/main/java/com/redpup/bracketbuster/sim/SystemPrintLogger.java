package com.redpup.bracketbuster.sim;

/**
 * A {@link Logger} that logs messages to {@link System#out}
 */
public final class SystemPrintLogger implements Logger {

  private long count;

  @Override
  public void log(String message) {
    System.out.println(message);
  }

  @Override
  public void setCurrentStep(String step) {
    System.out.println("==================================================");
    System.out.println(step);
    System.out.println("==================================================");
  }

  @Override
  public void setTotalMatchups(long totalMatchups) {
    count = 0;
  }

  @Override
  public void handleMatchup() {
    if (++count % 100000 == 0) {
      System.out.println("Calculating matchup..." + count);
    }
  }

  @Override
  public void handleOutput(Output output) {

  }
}
