package com.redpup.bracketbuster.sim;

/**
 * Interface for logging events during a simulation.
 */
public interface Logger {

  /**
   * Logs the given message.
   */
  void log(String message);

  /**
   * Sets the current iteration. Calling this again may override the previous value. This should
   * reset any internal counters.
   */
  void setIteration(int i);

  /**
   * Sets the current step that is executing. Calling this again may overwrite the previous step.
   */
  void setCurrentStep(String step);

  /**
   * Logs or otherwise updates the UI for the given match being handled.
   */
  void handleMatchup();

  /**
   * Logs or otherwise updates the UI for the given output. The runner is passed for access to
   * relevant params.
   */
  void handleOutput(Output output, Runner runner);
}
