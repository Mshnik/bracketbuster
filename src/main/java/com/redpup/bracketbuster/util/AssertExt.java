package com.redpup.bracketbuster.util;

import com.google.common.annotations.VisibleForTesting;

/**
 * Additional assertion methods for testing.
 */
@VisibleForTesting
public final class AssertExt {

  private AssertExt() {
  }

  /**
   * Wrapper for a runnable that throws a certain type of exception.
   */
  @FunctionalInterface
  @VisibleForTesting
  public interface ThrowingRunnable<E extends Exception> {

    /**
     * Executes this runnable. May throw {@link E}.
     */
    void run() throws E;
  }

  /**
   * Asserts that the given {@code throwingRunnable} throws an exception of type {@link E} when run.
   * If such an exception is thrown, returns it. Otherwise, fails with an {@link AssertionError}.
   */
  public static <E extends Exception> E assertThrows(
      Class<E> exceptionClass,
      ThrowingRunnable<E> throwingRunnable) {
    try {
      throwingRunnable.run();
    } catch (Exception e) {
      if (exceptionClass.isInstance(e)) {
        return exceptionClass.cast(e);
      }
      throw new AssertionError(String
          .format("Expected %s but got %s", exceptionClass.getCanonicalName(),
              e.getClass().getCanonicalName()));
    }

    throw new AssertionError(String.format("Expected %s to be thrown but nothing was thrown.",
        exceptionClass.getCanonicalName()));
  }

}
